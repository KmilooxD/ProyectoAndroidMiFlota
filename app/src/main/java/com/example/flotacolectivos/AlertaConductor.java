package com.example.flotacolectivos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;





public class AlertaConductor extends AppCompatActivity {

    Spinner spinerevento;
    Button btn_ingresarAlerta, btn_activarUbicacion;
    private Evento eventoSeleccionado;
    private AuntenticarUsuario autenticarUsuario; // Nueva variable de instancia
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean locationPermissionGranted = false;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000;  // 5 segundos en milisegundos
    private static final long FASTEST_INTERVAL = 2000; // 2 segundos en milisegundos
    private double latitud;
    private double longitud;
    private int idVehiculo;
    private String emaill;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta_conductor);

        // Crear la solicitud de ubicación
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        spinerevento = findViewById(R.id.spinnerTipoAlerta);
        btn_ingresarAlerta = findViewById(R.id.btnEnviarAlerta);
        btn_activarUbicacion = findViewById(R.id.button_activarUbicacion);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        solicitarPermisosYActualizarUbicacion();
        btn_activarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUbicacionHabilitada()) {
                    // La ubicación está habilitada, obtener la ubicación
                    actualizarUbicacion();
                } else {
                    // La ubicación no está habilitada, abrir configuración de ubicación
                    abrirConfiguracionUbicacion();
                }
            }
        });


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
                        actualizarUbicacion();


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
                    obtenerVehiculoPorIdConductor(idConductor);
                    // Obtener la fecha y hora actual
                    String fecha = obtenerFechaActual();
                    String hora = obtenerHoraActual();
                    int idEvento = eventoSeleccionado.getId();
                    registrarAlertaConductorint(idEvento, idConductor, fecha, hora,latitud,longitud);



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

    private void registrarAlertaConductorint(int idEvento, int fkIdConductor, String fecha, String hora, double latitud, double longitud) {
        ConexionServer.registrarAlertaConductor(idEvento, fkIdConductor, fecha, hora,latitud,longitud, new ConexionServer.OnServerResponseListener<Object>() {
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

    private void obtenerVehiculoPorIdConductor(int idConductor) {
        ConexionServer.obtenerVehiculoPorIdConductor(idConductor, new ConexionServer.OnServerResponseListener<Vehiculo>() {
            @Override
            public void onServerSuccess(String message) {
                // Puedes manejar el éxito si es necesario
            }

            @Override
            public void onServerResponse(Vehiculo response) {
                if (response != null) {
                    // Aquí puedes trabajar con el objeto Vehiculo obtenido
                    // Por ejemplo, mostrar información sobre el vehículo en la interfaz de usuario
                    String infoVehiculo = "Vehículo: " + response.getMarca() + " " + response.getModelo();
                    Toast.makeText(AlertaConductor.this, infoVehiculo, Toast.LENGTH_LONG).show();
                    Log.d("AlertaConductor", "ID del vehículo: " + response.getId());
                    int idVehiculo=response.getId();
                    //registrarUbicacion(latitud,longitud,idVehiculo);

                } else {
                    Toast.makeText(AlertaConductor.this, "Error: Respuesta nula al obtener el vehículo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onServerError(Exception e) {
                Log.d("Alertaconductor", "Error: " + e.getMessage());
                Toast.makeText(AlertaConductor.this, "Error al obtener el vehículo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

   /** private void registrarUbicacion(double latitud, double longitud, int fkIdVehiculo) {
        ConexionServer.registrarUbicacion(latitud, longitud, fkIdVehiculo, new ConexionServer.OnServerResponseListener<Object>() {
            @Override
            public void onServerSuccess(String message) {
                // Manejo de la respuesta exitosa
                Toast.makeText(AlertaConductor.this, "Ubicación registrada exitosamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServerResponse(Object response) {
                // Puedes manejar la respuesta del servidor si es necesario
            }

            @Override
            public void onServerError(Exception e) {
                // Manejo del error
                Toast.makeText(AlertaConductor.this, "Error al registrar la ubicación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
**/


    private void solicitarPermisosYActualizarUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Si ya se tienen permisos, actualizar el estado y obtener/actualizar ubicación
            locationPermissionGranted = true;

        } else {
            // Si no hay permisos, solicitarlos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el permiso es otorgado, establecer la bandera y obtener/actualizar ubicación
                locationPermissionGranted = true;
                actualizarUbicacion();
            } else {
                // Si se deniega el permiso, mostrar un mensaje
                Toast.makeText(this, "La aplicación necesita permisos de ubicación para funcionar correctamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void actualizarUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Tienes permisos, puedes obtener y actualizar la ubicación actual
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                actualizarUbicacionToast(location.getLatitude(), location.getLongitude());
                                latitud = location.getLatitude();
                                longitud = location.getLongitude();


                            } else {
                                //Toast.makeText(AlertaConductor.this, "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            // Iniciar actualizaciones de ubicación
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            // No tienes permisos, solicitar permisos de ubicación
            solicitarPermisosYActualizarUbicacion();
        }
    }

    // Método para actualizar el Toast con las nuevas coordenadas de latitud y longitud
    private void actualizarUbicacionToast(double latitud, double longitud) {
        String mensaje = "Latitud: " + latitud + ", Longitud: " + longitud;
        Toast.makeText(AlertaConductor.this, mensaje, Toast.LENGTH_SHORT).show();
    }


    // Crear un objeto LocationCallback para manejar las actualizaciones de ubicación
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                Location location = locationResult.getLastLocation();
                double latitud = location.getLatitude();
                double longitud = location.getLongitude();
                actualizarUbicacionToast(latitud, longitud);
            }
        }
    };

    // Detener las actualizaciones de ubicación cuando la actividad se pausa
    @Override
    protected void onPause() {
        super.onPause();
        detenerActualizacionUbicacion();
    }

    private void detenerActualizacionUbicacion() {
        fusedLocationClient.removeLocationUpdates(locationCallback);


    }

    private boolean isUbicacionHabilitada() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verificar la ubicación cuando la aplicación vuelve a estar en primer plano
        if (locationPermissionGranted) {
            if (isUbicacionHabilitada()) {
                actualizarUbicacion();
                Toast.makeText(AlertaConductor.this, "Ubicación activada", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(AlertaConductor.this, "Ubicación desactivada. Habilite la ubicación ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirConfiguracionUbicacion() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        // No intentes actualizar la ubicación aquí
    }

}