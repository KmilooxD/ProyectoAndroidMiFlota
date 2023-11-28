package com.example.flotacolectivos;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.HttpException;

public class AlertaConductor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta_conductor);


        // Obtener referencias de los elementos de la interfaz
        Spinner spinnerTipoAlerta = findViewById(R.id.spinnerTipoAlerta);
        Button btnEnviarAlerta = findViewById(R.id.btnEnviarAlerta);

        // Configurar el adaptador para el menú desplegable
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tipos_alerta,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Enlazar el adaptador al menú desplegable
        spinnerTipoAlerta.setAdapter(adapter);

        // Configurar el listener para el menú desplegable
        spinnerTipoAlerta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Manejar la selección del tipo de alerta
                String tipoAlerta = adapterView.getItemAtPosition(position).toString();
                // Puedes almacenar esta selección en una variable global si es necesario


                Log.d("AlertaConductor", "Tipo de alerta a enviar: " + tipoAlerta);

                System.out.println("la alerta seleccionada es: "+tipoAlerta);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Manejar el caso en que no se haya seleccionado nada
            }
        });

        // Configurar el listener para el botón de enviar alerta
        btnEnviarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el tipo de alerta seleccionado
                String tipoAlerta = spinnerTipoAlerta.getSelectedItem().toString();

                // Llamar a la función almacenarAlerta
                // En el método onServerResponse de la clase AlertaConductor
                almacenaralerta(tipoAlerta);
            }
        });

    }


    private void almacenaralerta (String seleccion){
        ConexionServer.almacenarAlerta(seleccion, new ConexionServer.OnServerResponseListener() {
            @Override
            public void onServerResponse(String response) {
                // Manejar la respuesta del servidor (opcional)
                Log.d("AlertaConductor", response);

                // Verificar si la respuesta contiene un mensaje de éxito
                if (response != null && response.contains("Alerta almacenada correctamente")) {
                    // Muestra un mensaje de éxito
                    Toast.makeText(AlertaConductor.this, "Alerta enviada con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    // Muestra un mensaje de error
                    Toast.makeText(AlertaConductor.this, "Error al enviar la alerta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onServerError(Exception e) {
                // Manejar el error del servidor (opcional)
                Log.e("AlertaConductor", "Error al enviar la alerta", e);

                if (e instanceof HttpException) {
                    // Manejar errores HTTP aquí
                    HttpException httpException = (HttpException) e;
                    int statusCode = httpException.code();
                    Log.e("AlertaConductor", "Código de error HTTP: " + statusCode);
                    // Aquí puedes agregar más información sobre el error, si es necesario
                }
            }


        });
    }
}
