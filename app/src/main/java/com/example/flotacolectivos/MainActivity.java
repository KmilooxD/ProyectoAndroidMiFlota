package com.example.flotacolectivos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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


            }

            @Override
            public void onServerResponse(Object  response) {
                // Este método no debería ser invocado en el caso de autenticarUsuario, pero implementarlo de todos modos
                // o lanzar una UnsupportedOperationException para indicar que no se espera este tipo de respuesta.
                // Manejar mensaje de éxito
                if (response.equals("Autenticación exitosa")) {
                    Intent intent = new Intent(MainActivity.this, AlertaConductor.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onServerError(Exception e) {
                // Manejar error de servidor
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}