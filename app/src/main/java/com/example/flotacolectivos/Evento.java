package com.example.flotacolectivos;

public class Evento {
    private String nombre;

    public Evento(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
