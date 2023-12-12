package com.example.flotacolectivos;

public class Ubicacion {
    private double latitud;
    private double longitud;
    private int fkIdVehiculo;

    public Ubicacion( double latitud, double longitud, int fkIdVehiculo) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.fkIdVehiculo = fkIdVehiculo;
    }

}
