package com.example.autoid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraHelper {
    private static final String TAG = "CameraHelper";
    private static final int REQUEST_CAMERA_PERMISSION = 9;
    CameraControl cameraControl;
    Camera camera;
    public float ZOOM_LENGTH = 0.6f;
    private final Context context;
    private final ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    public CameraHelper(Context context) {
        this.context = context;
    }
    public void inicializarCamara(PreviewView previewView, CameraSelector cameraSelector, ImageAnalysis.Analyzer analyzer) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera(previewView, cameraSelector, analyzer);
        }
    }

    private void startCamera(PreviewView previewView, CameraSelector cameraSelector, ImageAnalysis.Analyzer analyzer) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this.context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, previewView, cameraSelector, analyzer);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error al iniciar la cámara", e);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider, PreviewView previewView, CameraSelector cameraSelector, ImageAnalysis.Analyzer analyzer) {
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .setTargetResolution(new Size(640, 640))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, analyzer);

        camera = cameraProvider.bindToLifecycle((AppCompatActivity) context, cameraSelector, preview, imageAnalysis);
        cameraControl = camera.getCameraControl();
        cameraControl.setLinearZoom((float) ZOOM_LENGTH);
        cameraControl.setZoomRatio((float) 1.2);
    }
    public void zoomIn() {
        float incrementoZoom = 0.1f;
        float zoomMaximo = 0.8f;

        if (ZOOM_LENGTH + incrementoZoom <= zoomMaximo) {
            ZOOM_LENGTH += incrementoZoom;
            cameraControl.setLinearZoom(ZOOM_LENGTH);
        }
    }

    public void zoomOut() {
        float decrementoZoom = 0.1f;
        float zoomMinimo = 0.2f;

        if (ZOOM_LENGTH - decrementoZoom >= zoomMinimo) {
            ZOOM_LENGTH -= decrementoZoom;
            cameraControl.setLinearZoom(ZOOM_LENGTH);
        }
    }
    public void liberarRecursosDeCamara() {
        if (cameraProviderFuture != null) {
            cameraProviderFuture.addListener(() -> {
                ProcessCameraProvider cameraProvider;
                try {
                    cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Error al liberar recursos de la cámara", e);
                }
            }, ContextCompat.getMainExecutor(context));
            cameraExecutor.shutdown();
        }
    }
}
