package com.example.autoid;

import static com.example.autoid.Modelos.Constantes.Base_Url;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ApiClient es una clase que configura y proporciona una instancia de Retrofit para realizar
 * solicitudes HTTP a un servidor REST.
 */
public class ApiClient {
    private final Retrofit retrofit; // Instancia de Retrofit
    private final HttpLoggingInterceptor loggin; // Interceptor de logging para registrar detalles de las solicitudes HTTP

    /**
     * Constructor de ApiClient que configura Retrofit con un interceptor de logging y tiempos de espera.
     */
    public ApiClient() {
        loggin = new HttpLoggingInterceptor();
        loggin.setLevel(HttpLoggingInterceptor.Level.BODY); // Configura el nivel de logging

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(loggin)
                .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para la conexión
                .writeTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para la escritura
                .readTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para la lectura
                .callTimeout(30, TimeUnit.SECONDS); // Tiempo de espera para la llamada

        // Configura Retrofit con la URL base y el convertidor de JSON a objetos
        retrofit = new Retrofit.Builder()
                .baseUrl(Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    /**
     * Proporciona la instancia de Retrofit configurada.
     *
     * @return La instancia de Retrofit.
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }
}
