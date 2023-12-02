package com.example.flotacolectivos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlertaConductor extends AppCompatActivity {

    Spinner spinerevento;
    Button btn_ingresarAlerta;
    private Evento eventoSeleccionado;
    private AuntenticarUsuario autenticarUsuario; // Nueva variable de instancia

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta_conductor);

        spinerevento = findViewById(R.id.spinnerTipoAlerta);
        btn_ingresarAlerta = findViewById(R.id.btnEnviarAlerta);

        // Llamada a la API para obtener eventos
        ConexionServer.obtenerEventosDesdeServidor(new ConexionServer.OnServerResponseListener<List<Evento>>() {
            @Override
            public void onServerSuccess(String message) {
                // Lógica para éxito (si es necesario)
            }

            @Override
            public void onServerResponse(List<Evento> eventos) {
                // Lógica para manejar la lista de eventos
                // Actualizar el spinner con la lista de eventos
                ArrayAdapter<Evento> adapter = new ArrayAdapter<>(AlertaConductor.this, android.R.layout.simple_spinner_item, eventos);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinerevento.setAdapter(adapter);
            }

            @Override
            public void onServerError(Exception e) {
                // Lógica para manejar el error
                Toast.makeText(AlertaConductor.this, "Error al obtener eventos", Toast.LENGTH_SHORT).show();
            }
        });

        // Obtener el AuntenticarUsuario de la intención al iniciar la actividad por primera vez
        autenticarUsuario = obtenerAuntenticarUsuarioDeIntent();

        btn_ingresarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el evento seleccionado del spinner
                eventoSeleccionado = (Evento) spinerevento.getSelectedItem();

                // Verificar que se haya seleccionado un evento
                if (eventoSeleccionado != null) {
                    // Verificar si se obtuvo correctamente el AuntenticarUsuario
                    if (autenticarUsuario != null) {
                        // Obtener el email del AuntenticarUsuario
                        String email = autenticarUsuario.getEmail();

                        // Hacer algo con el email y la contraseña...
                        obtenerIdConductor(email);

                    } else {
                        Toast.makeText(AlertaConductor.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                    }

                    int idEvento = eventoSeleccionado.getId();
                    Toast.makeText(AlertaConductor.this, "ID del evento seleccionado: " + idEvento, Toast.LENGTH_SHORT).show();
                    // Aquí puedes utilizar el idEvento según tus necesidades
                } else {
                    Toast.makeText(AlertaConductor.this, "Seleccione un evento primero", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void obtenerIdConductor(String email) {
        ConexionServer.obtenerIdConductor(email, new ConexionServer.OnServerResponseListener<JsonObject>() {
            @Override
            public void onServerSuccess(String message) {
                Toast.makeText(AlertaConductor.this, "Autenticación exitosa", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServerResponse(JsonObject response) {
                // Manejar la respuesta del servidor (obtuvimos el ID del conductor)
                if (response != null && response.has("result") && response.getAsJsonObject("result").has("Fk_IdConductor")) {
                    int idConductor = response.getAsJsonObject("result").get("Fk_IdConductor").getAsInt();
                    Toast.makeText(AlertaConductor.this, "ID conductor: " + idConductor, Toast.LENGTH_LONG).show();
                    Log.d("AlertaConductor", "ID conductor: " + idConductor);

                    // Obtener la fecha y hora actual
                    String fecha = obtenerFechaActual();
                    String hora = obtenerHoraActual();
                    int idEvento = eventoSeleccionado.getId();
                    registrarAlertaConductorint(idEvento, idConductor, fecha, hora);
                } else {
                    Toast.makeText(AlertaConductor.this, "Error: Respuesta nula o falta la clave Fk_IdConductor", Toast.LENGTH_SHORT).show();
                    Log.e("AlertaConductor", "Error: Respuesta nula o falta la clave Fk_IdConductor");
                }
            }

            @Override
            public void onServerError(Exception e) {
                // Manejar error de servidor al obtener el ID del conductor
                Toast.makeText(AlertaConductor.this, "Error al obtener el ID del conductor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para obtener el AuntenticarUsuario de la intención
    private AuntenticarUsuario obtenerAuntenticarUsuarioDeIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("autenticarUsuario")) {
            return (AuntenticarUsuario) intent.getSerializableExtra("autenticarUsuario");
        } else {
            return null;
        }
    }

    // Funciones auxiliares para obtener la fecha y la hora actual
    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String obtenerHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void registrarAlertaConductorint(int idEvento, int fkIdConductor, String fecha, String hora) {
        ConexionServer.registrarAlertaConductor(idEvento, fkIdConductor, fecha, hora, new ConexionServer.OnServerResponseListener<Object>() {
            @Override
            public void onServerSuccess(String message) {
                Toast.makeText(AlertaConductor.this, "Alerta del conductor registrada exitosamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServerResponse(Object response) {
                // Puedes manejar la respuesta del servidor si es necesario
            }

            @Override
            public void onServerError(Exception e) {
                Toast.makeText(AlertaConductor.this, "Error al registrar la alerta del conductor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
