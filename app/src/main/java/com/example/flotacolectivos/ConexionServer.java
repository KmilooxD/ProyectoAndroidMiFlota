package com.example.flotacolectivos;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConexionServer {

    // Interfaz para manejar los resultados de las solicitudes
    public interface OnServerResponseListener {
        void onServerResponse(String response);
        void onServerError(Exception e);
    }

    public static void obtenerDatos(OnServerResponseListener listener) {
        String url = "http://192.168.249.46:3000/obtenerDatos";

        try {
            new ServerRequestTask(listener, "GET", null).execute(url);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onServerError(e);
        }
    }

    // AsyncTask para realizar solicitudes al servidor en segundo plano
    private static class ServerRequestTask extends AsyncTask<String, Void, String> {
        private OnServerResponseListener listener;
        private String requestMethod; // GET o POST
        private String jsonInputString; // Datos JSON para la solicitud POST

        ServerRequestTask(OnServerResponseListener listener, String requestMethod, String jsonInputString) {
            this.listener = listener;
            this.requestMethod = requestMethod;
            this.jsonInputString = jsonInputString;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if ("GET".equals(requestMethod)) {
                    return hacerSolicitudGET(params[0]);
                } else if ("POST".equals(requestMethod)) {
                    return hacerSolicitudPOST(params[0], jsonInputString);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                listener.onServerResponse(result);
            } else {
                listener.onServerError(new IOException("Error de conexión"));
            }
        }

        // Método para realizar una solicitud GET al servidor
        private String hacerSolicitudGET(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } finally {
                urlConnection.disconnect();
            }
        }

        // Método para realizar una solicitud POST al servidor con datos JSON
        private String hacerSolicitudPOST(String urlString, String jsonInputString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                urlConnection.setDoOutput(true);

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } finally {
                urlConnection.disconnect();
            }
        }
    }

}
