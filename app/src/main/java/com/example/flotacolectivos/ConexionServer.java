package com.example.flotacolectivos;

import android.content.Intent;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ConexionServer {

    private static final String BASE_URL = "http://192.168.249.46:3000/";
    private static ServicoAPI apiService;

    public interface OnServerResponseListener<T> {
        void onServerSuccess(String message);
        void onServerResponse(T response);
        void onServerError(Exception e);
    }



    public static void autenticarUsuario(String email, String contrasena, OnServerResponseListener listener) {
        AuntenticarUsuario request = new AuntenticarUsuario(email, contrasena);

        Call<Void> call = getApiService().autenticarUsuario(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Éxito
                    listener.onServerResponse("Autenticación exitosa");

                } else {
                    try {
                        // Manejo del error
                        JSONObject errorBody = new JSONObject(response.errorBody().string());
                        String errorMessage = errorBody.optString("message", "Error de autenticación");
                        listener.onServerError(new Exception(errorMessage));
                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onServerError(new Exception("Error de autenticación"));
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Error de conexión
                listener.onServerError(new Exception("Error de conexión"));
            }
        });
    }

    public static void obtenerEventosDesdeServidor(OnServerResponseListener<List<Evento>> listener) {
        Call<List<Evento>> call = getApiService().obtenerNombresEventos();
        call.enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                if (response.isSuccessful()) {
                    // Éxito
                    Log.d("API", "Respuesta exitosa: " + response.body());
                    listener.onServerResponse(response.body());
                } else {
                    // Manejo del error
                    Log.e("API", "Error en la respuesta del servidor: " + response.code());
                    listener.onServerError(new Exception("Error en la respuesta del servidor: " + response.code()));
                }
            }


            @Override
            public void onFailure(Call<List<Evento>> call, Throwable t) {
                // Error de conexión
                Log.e("API", "Error de conexión: " + t.getMessage());
                t.printStackTrace(); // Agrega esta línea para imprimir el stack trace completo
                listener.onServerError(new Exception("Error de conexión", t));
            }

        });
    }





    private static ServicoAPI getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ServicoAPI.class);
        }
        return apiService;
    }
}