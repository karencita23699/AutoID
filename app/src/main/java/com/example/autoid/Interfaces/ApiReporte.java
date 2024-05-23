package com.example.autoid.Interfaces;

import com.example.autoid.Modelos.Reporte;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiReporte {
    @POST("Reporte/CrearReporte.php")
    Call<Void> crearReporte(@Body JsonObject jsonObject);
    @GET("Reporte/ObtenerReportes.php")
    Call<List<Reporte>> obtenerReportes(@Query("idUsuario") int idUsuario);
    @PUT("Reporte/EliminarReporte.php")
    Call<Void> eliminarReporte(@Body JsonObject object);
}
