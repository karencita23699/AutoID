package com.example.autoid.Interfaces;

import com.example.autoid.Modelos.Usuario;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiUsuario {
    @POST("Usuario/CrearUsuario.php")
    Call<Integer> crearUsuario(@Body Usuario usuario);
    @GET("Usuario/ObtenerUsuario.php")
    Call<Usuario> obtenerUsuario(@Query("idUsuario") int idUsuario);
    @GET("Usuario/VerificarEmail.php")
    Call<Void> verificarEmail(@Query("correo") String correo);
    @GET("Usuario/VerificarNomUsuario.php")
    Call<Void> verificarNomUsuario(@Query("nomUsuario") String nomUsuario);
    @FormUrlEncoded
    @POST("Usuario/Login.php")
    Call<Usuario> login(
            @Field("Usuario") String us,
            @Field("Contrasena") String pass
    );
    @PUT("Usuario/ModificarUsuario.php")
    Call<Void> modificarUsuario(@Body JsonObject object);

}
