package com.example.autoid.Interfaces;

import com.example.autoid.Modelos.Placa;
import com.example.autoid.Modelos.Reporte;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiPlaca {
    @POST("Placa/CrearPlaca.php")
    Call<Void> crearPlaca(@Body JsonObject jsonObject);
    @GET("Placa/ObtenerPlacas.php")
    Call<List<Placa>> obtenerRegistros(@Query("idUsuario") int idUsuario);
    @PUT("Placa/EliminarRegistro.php")
    Call<Void> eliminarRegistro(@Body JsonObject object);
}
