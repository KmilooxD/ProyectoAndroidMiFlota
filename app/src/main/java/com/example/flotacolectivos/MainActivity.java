package com.example.flotacolectivos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextContrasena;
    private Button buttonIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.editTextTextEmailAddress3);
        editTextContrasena = findViewById(R.id.editTextTextPassword2);
        buttonIniciarSesion = findViewById(R.id.button2);

        // Asignar un listener al botón de inicio de sesión
        buttonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el correo y la contraseña ingresados por el usuario
                String email = editTextEmail.getText().toString();
                String contrasena = editTextContrasena.getText().toString();

                // Realizar la solicitud al servidor para autenticar al usuario
                autenticarUsuario(email, contrasena);
            }
        });
    }

    private void autenticarUsuario(String email, String contrasena) {
        // Realizar la solicitud al servidor para autenticar al usuario


        ConexionServer.autenticarUsuario(email, contrasena, new ConexionServer.OnServerResponseListener<Object>() {
            @Override
            public void onServerSuccess(String message) {
                Toast.makeText(MainActivity.this, "Autenticación exitosa", Toast.LENGTH_SHORT).show();
                obtenerIdConductor(email);
                Intent intent = new Intent(MainActivity.this, AlertaConductor.class);
                startActivity(intent);

            }

            @Override
            public void onServerResponse(Object  response) {
                // Este método no debería ser invocado en el caso de autenticarUsuario, pero implementarlo de todos modos
                // o lanzar una UnsupportedOperationException para indicar que no se espera este tipo de respuesta.
                // Manejar mensaje de éxito
                if (response.equals("Autenticación exitosa")) {
                    obtenerIdConductor(email);
                }

            }

            @Override
            public void onServerError(Exception e) {
                // Manejar error de servidor
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void obtenerIdConductor(String email) {
        ConexionServer.obtenerIdConductor(email, new ConexionServer.OnServerResponseListener<JsonObject>() {
            @Override
            public void onServerSuccess(String message) {
                Toast.makeText(MainActivity.this, "Autenticación exitosa", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServerResponse(JsonObject response) {
                // Manejar la respuesta del servidor (obtuvimos el ID del conductor)
                if (response != null && response.has("result") && response.getAsJsonObject("result").has("Fk_IdConductor")) {
                    int idConductor = response.getAsJsonObject("result").get("Fk_IdConductor").getAsInt();
                    Toast.makeText(MainActivity.this, "ID conductor: " + idConductor, Toast.LENGTH_LONG).show();
                    Log.d("MainActivity", "ID conductor: " + idConductor);
                    Intent intent = new Intent(MainActivity.this, AlertaConductor.class);
                    startActivity(intent);
                    // Puedes almacenar este ID en algún lugar si es necesario
                    // Ahora puedes usar este ID para otras operaciones
                } else {
                    Toast.makeText(MainActivity.this, "Error: Respuesta nula o falta la clave Fk_IdConductor", Toast.LENGTH_SHORT).show();
                    Log.e("MainActivity", "Error: Respuesta nula o falta la clave Fk_IdConductor");
                }
            }




            @Override
            public void onServerError(Exception e) {
                // Manejar error de servidor al obtener el ID del conductor
                Toast.makeText(MainActivity.this, "Error al obtener el ID del conductor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}