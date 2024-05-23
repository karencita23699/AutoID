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

public class ImageProcessor {
    private Context context;
    private static final int INPUT_SIZE = 640;

    private LastFp161 model;
    private Float nmsConfidence = 0.5f;

    private final int[] OUTPUT_SIZE = new int[]{1, 25200, 85};
    private String[] labels = new String[]{
            "1","2","3","4","5","6","7","8","9","0","A","B","C","D","E","F","G","H","I","J","K",
            "L","M","N","O","P","Q","R","S","T","U","W","X","Y","Z","Placa"
    };

    public ImageProcessor(Context context) {
        this.context = context;
        initializeModel();
    }

    private void initializeModel() {
        try {
            model = LastFp161.newInstance(context);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al inicializar el modelo", Toast.LENGTH_SHORT).show();
        }
    }

    public List<Detection> processImage(Bitmap bitmap, Context context, float confidenceThreshold) throws IOException {
        List<Detection> Ldet = new ArrayList<>();
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 640, 640, 3}, DataType.FLOAT32);
        inputFeature0.loadBuffer(convertBitmapToByteBuffer(bitmap));
        LastFp161.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

        float[] salida = outputFeature0.getFloatArray();
        int[] outputShape = outputFeature0.getShape();
        int numclasses=36;

        float[][] outputVectorFlat = new float[outputShape[1]][outputShape[2]];
        int index = 0, k=0;
        for (int i = 0; i < outputShape[1]; i++) {
            int ind = index;
            for (int j = 0; j < outputShape[2]; j++) {
                if(salida[ind+4]>confidenceThreshold){
                    outputVectorFlat[k][j]=salida[index++];
                    if(j==outputShape[2]-1)
                        k++;
                }else{
                    index+=outputShape[2];
                    j=outputShape[2];
                }
            }
        }
        for (int i = 0; i <k; i++) {
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
                for(int j=5;j<(numclasses+5);j++){
                    if(outputVectorFlat[i][j]>0.75f)
                    {
                        det.setPresicion(outputVectorFlat[i][j]);
                        det.setClassIndex(j-5);
                        det.setClase(labels[(j-5)]);
                        Ldet.add(det);
                    }
                }
            }
        }
        Ldet = applyNMS(Ldet, nmsConfidence);
        model.close();
        return Ldet;
    }


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
