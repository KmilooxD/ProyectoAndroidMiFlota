package com.example.flotacolectivos;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface ServicoAPI {
    @POST("autenticarUsuario")
    Call<Void> autenticarUsuario(@Body AuntenticarUsuario request);

    @POST("enviarAlerta")
    Call<Void> almacenarAlerta(@Body TipoAlerta request);
}

