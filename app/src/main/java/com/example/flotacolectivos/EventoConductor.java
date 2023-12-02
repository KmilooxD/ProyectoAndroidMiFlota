package com.example.flotacolectivos;

public class EventoConductor {
    private int Fk_IdEvento;
    private int Fk_IdConductor;
    String Fecha;
    String Hora;

    public EventoConductor(int fk_IdEvento, int fk_IdCondcutor, String fecha, String hora) {
        Fk_IdEvento = fk_IdEvento;
        Fk_IdConductor = fk_IdCondcutor;
        Fecha = fecha;
        Hora = hora;
    }
}
