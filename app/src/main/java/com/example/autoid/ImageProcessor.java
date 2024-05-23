package com.example.autoid;

import static com.example.autoid.NMS.applyNMS;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.example.autoid.Modelos.BoundingBox;
import com.example.autoid.Modelos.Detection;
import com.example.autoid.ml.LastFp161;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * La clase ImageProcessor se encarga de procesar imágenes usando un modelo de aprendizaje automático para detectar
 * ciertos objetos en la imagen. Utiliza TensorFlow Lite para la inferencia del modelo y realiza un post-procesamiento
 * para obtener las detecciones finales.
 */
public class ImageProcessor {
    private Context context; // Contexto de la aplicación
    private static final int INPUT_SIZE = 640; // Tamaño de la imagen de entrada (640x640)

    private LastFp161 model; // Instancia del modelo LastFp161
    private Float nmsConfidence = 0.5f; // Umbral de confianza para la supresión de no-máximos (NMS)

    private final int[] OUTPUT_SIZE = new int[]{1, 25200, 85}; // Tamaño de la salida del modelo
    private String[] labels = new String[]{
        "1","2","3","4","5","6","7","8","9","0","A","B","C","D","E","F","G","H","I","J","K",
        "L","M","N","O","P","Q","R","S","T","U","W","X","Y","Z","Placa"
    }; // Array de etiquetas que corresponden a las clases que el modelo puede detectar

    /**
     * Constructor de ImageProcessor. Inicializa el ImageProcessor y el modelo de TensorFlow Lite.
     * @param context Contexto de la aplicación.
     */
    public ImageProcessor(Context context) {
        this.context = context;
        initializeModel();
    }

    /**
     * Inicializa el modelo de TensorFlow Lite. Si ocurre un error, muestra un mensaje Toast.
     */
    private void initializeModel() {
        try {
            model = LastFp161.newInstance(context);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al inicializar el modelo", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Procesa una imagen y devuelve una lista de detecciones.
     * @param bitmap Imagen a procesar.
     * @param context Contexto de la aplicación.
     * @param confidenceThreshold Umbral de confianza para considerar una detección válida.
     * @return Lista de detecciones (List<Detection>).
     * @throws IOException Puede lanzar IOException.
     */
    public List<Detection> processImage(Bitmap bitmap, Context context, float confidenceThreshold) throws IOException {
        List<Detection> Ldet = new ArrayList<>();
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 640, 640, 3}, DataType.FLOAT32);
        inputFeature0.loadBuffer(convertBitmapToByteBuffer(bitmap));
        LastFp161.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

        float[] salida = outputFeature0.getFloatArray();
        int[] outputShape = outputFeature0.getShape();
        int numclasses = 36;

        float[][] outputVectorFlat = new float[outputShape[1]][outputShape[2]];
        int index = 0, k = 0;
        for (int i = 0; i < outputShape[1]; i++) {
            int ind = index;
            for (int j = 0; j < outputShape[2]; j++) {
                if (salida[ind + 4] > confidenceThreshold) {
                    outputVectorFlat[k][j] = salida[index++];
                    if (j == outputShape[2] - 1)
                        k++;
                } else {
                    index += outputShape[2];
                    j = outputShape[2];
                }
            }
        }
        for (int i = 0; i < k; i++) {
            float presicion = outputVectorFlat[i][4];
            if (presicion > confidenceThreshold) {
                BoundingBox caja = new BoundingBox();
                caja.setX(outputVectorFlat[i][0]);
                caja.setY(outputVectorFlat[i][1]);
                caja.setWidth(outputVectorFlat[i][2]);
                caja.setHeight(outputVectorFlat[i][3]);
                Detection det = new Detection();
                det.setBoundingBox(caja);
                det.setConfidence(outputVectorFlat[i][4]);
                for (int j = 5; j < (numclasses + 5); j++) {
                    if (outputVectorFlat[i][j] > 0.75f) {
                        det.setPresicion(outputVectorFlat[i][j]);
                        det.setClassIndex(j - 5);
                        det.setClase(labels[(j - 5)]);
                        Ldet.add(det);
                    }
                }
            }
        }
        Ldet = applyNMS(Ldet, nmsConfidence);
        model.close();
        return Ldet;
    }

    /**
     * Convierte una imagen Bitmap a un ByteBuffer compatible con el modelo de TensorFlow Lite.
     * @param bitmap Imagen a convertir.
     * @return ByteBuffer que representa la imagen.
     */
    protected ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 1 * INPUT_SIZE * INPUT_SIZE * 3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                byteBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }
        return byteBuffer;
    }
}
