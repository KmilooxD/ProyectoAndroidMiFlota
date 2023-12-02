package com.example.flotacolectivos;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class AlertaConductor extends AppCompatActivity {

    Spinner spinerevento;
    Button btn_ingresarAlerta;
    private Evento eventoSeleccionado;

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

        btn_ingresarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el evento seleccionado del spinner
                Evento eventoSeleccionado = (Evento) spinerevento.getSelectedItem();

                // Verificar que se haya seleccionado un evento y obtener su ID
                if (eventoSeleccionado != null) {
                    int idEvento = eventoSeleccionado.getId();
                    Toast.makeText(AlertaConductor.this, "ID del evento seleccionado: " + idEvento, Toast.LENGTH_SHORT).show();

                    // Aquí puedes utilizar el idEvento según tus necesidades
                } else {
                    Toast.makeText(AlertaConductor.this, "Seleccione un evento primero", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}