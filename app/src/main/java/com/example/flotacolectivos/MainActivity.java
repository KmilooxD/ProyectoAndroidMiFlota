package com.example.flotacolectivos;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView resultadoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultadoTextView = findViewById(R.id.resultadoTextView);

        // Llamar al método obtenerDatos de ConexionServer
        ConexionServer.obtenerDatos(new ConexionServer.OnServerResponseListener() {
            @Override
            public void onServerResponse(String response) {
                // Manejar la respuesta del servidor aquí
                mostrarResultado(response);
            }

            @Override
            public void onServerError(Exception e) {
                // Manejar errores de conexión aquí
                mostrarResultado("Error de conexión");
            }
        });
    }

    // Método para mostrar el resultado en el TextView
    private void mostrarResultado(String resultado) {
        resultadoTextView.setText(resultado);
    }
}
