package com.example.flotacolectivos;

public class Evento {
    private String nombre;
    private int id;

    public Evento(String nombre, int id) {
        this.nombre = nombre;
        this.id=id;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return nombre;
    }
}