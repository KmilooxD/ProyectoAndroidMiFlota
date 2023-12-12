package com.example.flotacolectivos;
import com.google.gson.JsonObject;

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

    @GET("obtenerIdConductor/{email}")
    Call<JsonObject> obtenerIdConductor(@Path("email") String email);

    @POST("registrarAlertaConductor/{idEvento}")
    Call<Void> registrarAlertaConductor(@Path("idEvento") int idEvento,@Body EventoConductor request);

    @GET("obtenerVehiculoPorIdConductor/{idConductor}")
    Call<Vehiculo> obtenerVehiculoPorIdConductor(@Path("idConductor") int idConductor);



}