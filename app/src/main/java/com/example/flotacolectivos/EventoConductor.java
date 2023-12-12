package com.example.flotacolectivos;

public class EventoConductor {
    private int Fk_IdEvento;
    private int Fk_IdConductor;
    String Fecha;
    String Hora;
    double Latitud;
    double Longitud;

    public EventoConductor(int fk_IdEvento, int fk_IdConductor, String fecha, String hora, double latitud, double longitud) {
        Fk_IdEvento = fk_IdEvento;
        Fk_IdConductor = fk_IdConductor;
        Fecha = fecha;
        Hora = hora;
        Latitud = latitud;
        Longitud = longitud;
    }
}
