package com.example.flotacolectivos;
import  java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServicoAPI {
    @POST("autenticarUsuario")
    Call<Void> autenticarUsuario(@Body AuntenticarUsuario request);

    @GET("obtenerNombresEventos")
    Call<List<Evento>> obtenerNombresEventos();

    @GET("obtenerIdEvento/{nombre}")
    Call<Integer> obtenerIdEvento(@Path("nombre") String nombre);
}