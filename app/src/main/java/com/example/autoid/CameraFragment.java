package com.example.autoid;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoid.Interfaces.ApiPlaca;
import com.example.autoid.Interfaces.ApiReporte;
import com.example.autoid.Modelos.Caja;
import com.example.autoid.Modelos.Detection;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraFragment extends Fragment implements View.OnClickListener {
    PreviewView previewView;
    EditText etPlaca;
    Button btnPrediccion, btnCorrecto, btnIncorrecto;
    ImageView imgZoomMas, imgZoomMenos, imgCajaPrediccion, imgInfo;
    View view;
    Bitmap img;
    Paint text = new Paint();
    Paint box = new Paint();
    private CameraHelper cameraHelper;
    int Size = 640, left, top;
    int width_img_org, height_img_org;
    float confidenceThreshold = 0.75f, confidence = 0.75f;
    String prediccion = "";
    ImageProcessor imageProcessor;
    RecyclerView rvPredicciones;
    AdapterPredicciones adapterPredicciones;
    List<Detection> Ldet, listAdapter;
    String imagen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera, container, false);
        inicializar();
        cameraHelper.liberarRecursosDeCamara();
        cameraHelper.inicializarCamara(previewView, CameraSelector.DEFAULT_BACK_CAMERA, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                try {
                    Bitmap bitmap = image.toBitmap();
                    img = normalizar(bitmap);
                    width_img_org = img.getWidth();
                    height_img_org = img.getHeight();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    image.close();
                }
            }
        });
        return view;
    }

    private void inicializar() {
        previewView = view.findViewById(R.id.previewViewCamera);
        etPlaca = view.findViewById(R.id.etPlaca1);
        btnPrediccion = view.findViewById(R.id.btnPrediccion);
        btnCorrecto = view.findViewById(R.id.btnCorrecto);
        btnIncorrecto = view.findViewById(R.id.btnIncorrecto);
        imgZoomMas = view.findViewById(R.id.imgZoommas);
        imgZoomMenos = view.findViewById(R.id.imgZoommenos);
        imgCajaPrediccion = view.findViewById(R.id.viewprueba);
        rvPredicciones = view.findViewById(R.id.recyclerViewPredicciones);
        imgInfo = view.findViewById(R.id.imgInfo);

        rvPredicciones.setHasFixedSize(true);
        rvPredicciones.setLayoutManager(new LinearLayoutManager(view.getContext()));

        cameraHelper = new CameraHelper(this.getContext());
        imageProcessor = new ImageProcessor(this.getContext());

        text.setTextSize(80);
        text.setColor(Color.MAGENTA);

        box.setStyle(Paint.Style.STROKE);
        box.setColor(Color.MAGENTA);
        box.setStrokeWidth(5);

        btnPrediccion.setOnClickListener(this);
        btnCorrecto.setOnClickListener(this);
        btnIncorrecto.setOnClickListener(this);
        imgZoomMas.setOnClickListener(this);
        imgZoomMenos.setOnClickListener(this);
        imgInfo.setOnClickListener(this);
    }

    private Bitmap normalizar(Bitmap image) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        left = (image.getWidth() - Size) / 2;
        top = (image.getHeight() - Size) / 2;
        Bitmap imgnormalizada = Bitmap.createBitmap(image, left, top, Size, Size, matrix, true);

        return imgnormalizada;
    }

    private void predecir(ImageProcessor im, Bitmap img) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imagen = getStringImage(img);
                Ldet = null;
                try {
                    Ldet = im.processImage(img, requireContext(), confidenceThreshold);
                } catch (IOException e) {
                    Log.i("Error al Procesar", "No se pudo procesar la imagen " + e.getMessage());
                }

                Collections.sort(Ldet, (obj1, obj2) -> Float.compare(obj1.getBoundingBox().getX(), obj2.getBoundingBox().getX()));

                Bitmap cajaDet = Bitmap.createBitmap(previewView.getWidth(), previewView.getHeight(), Bitmap.Config.ARGB_8888);
                cajaDet.eraseColor(Color.TRANSPARENT);
                Canvas canvas = new Canvas(cajaDet);

                Caja caja = new Caja(0, 0, 0, 0);
                prediccion = "";
                for (Detection det : Ldet) {
                    if (det.getPresicion() > confidence && det.getConfidence() > confidence) {
                        if (!det.getClase().equals("Placa")) {
                            prediccion += det.getClase() + "";
                            listAdapter.add(det);
                        } else {
                            caja = Localizacion(det);
                            canvas.drawRect(caja.getX_min(), caja.getY_min(), caja.getX_max(), caja.getY_max(), box);
                        }
                    }
                }
                canvas.drawText(prediccion, caja.getX_min(), caja.getY_min() - 5, text);
                imgCajaPrediccion.setImageBitmap(cajaDet);
            }
        });
    }

    private Caja Localizacion(Detection det) {
        float x_center = det.getBoundingBox().getX() * previewView.getWidth();
        float y_center = det.getBoundingBox().getY() * previewView.getWidth();
        float width = det.getBoundingBox().getWidth() * previewView.getWidth();
        float height = det.getBoundingBox().getHeight() * previewView.getWidth();

        float x_min = Math.max(0, x_center - (width / 2));
        float y_min = Math.max(0, y_center - (height / 2));
        float x_max = Math.min(previewView.getWidth(), x_center + (width / 2));
        float y_max = Math.min(previewView.getHeight(), y_center + (height / 2));

        Caja caja = new Caja(x_min, y_min, x_max, y_max);
        return caja;
    }

    private void actualizarAdapter() {
        etPlaca.setText(prediccion);
        adapterPredicciones = new AdapterPredicciones(getActivity(), listAdapter);
        rvPredicciones.setAdapter(adapterPredicciones);
        adapterPredicciones.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnIncorrecto)
            Reportar();
        if (v.getId() == R.id.btnCorrecto)
            RegistrarPlaca();

        if (v.getId() == R.id.btnPrediccion) {
            etPlaca.setText("");
            listAdapter = new ArrayList<>();
            ImageProcessor im = new ImageProcessor(requireContext());
            predecir(im, img);
        }
        if (v.getId() == R.id.imgZoommas)
            cameraHelper.zoomIn();
        if (v.getId() == R.id.imgZoommenos)
            cameraHelper.zoomOut();
        if (v.getId() == R.id.imgInfo)
            if (!prediccion.equals(""))
                actualizarAdapter();
    }

    private void Reportar() {
        if (!prediccion.equals("")) {

            int idUsuario = ((MainActivity) getActivity()).getUserId();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("resultadoObtenido", prediccion);
            jsonObject.addProperty("idUsuario", idUsuario);
            jsonObject.addProperty("img", imagen);

            ApiClient apiClient = new ApiClient();
            ApiReporte apiReporte = apiClient.getRetrofit().create(ApiReporte.class);

            Call<Void> call = apiReporte.crearReporte(jsonObject);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful() && response.code() == 201) {
                        Toast.makeText(getContext(), "Reporte creado con exito", Toast.LENGTH_SHORT).show();
                        prediccion = "";
                    } else if (response.code() == 500)
                        Toast.makeText(getContext(), "Error al reportar", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        } else
            Toast.makeText(getContext(), "No se capturo ninguna placa", Toast.LENGTH_SHORT).show();
    }

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void RegistrarPlaca() {
        if (!prediccion.equals("")) {
            ApiClient apiClient = new ApiClient();
            ApiPlaca apiPlaca = apiClient.getRetrofit().create(ApiPlaca.class);

            int idUsuario = ((MainActivity) getActivity()).getUserId();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("numMatricula", prediccion);
            jsonObject.addProperty("idUsuario", idUsuario);

            Call<Void> call = apiPlaca.crearPlaca(jsonObject);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful() && response.code() == 201) {
                        Toast.makeText(getContext(), "Placa registrada exitosamente", Toast.LENGTH_SHORT).show();
                        prediccion = "";
                    } else if (response.code() == 500) {
                        Toast.makeText(getContext(), "Error al registrar la placa", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraHelper.liberarRecursosDeCamara();
    }
}