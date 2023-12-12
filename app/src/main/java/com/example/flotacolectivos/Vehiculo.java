package com.example.flotacolectivos;

public class Vehiculo {
    private int Id;
    private String Patente;
    private String Marca;
    private String Modelo;
    private int Kilometraje;

    public Vehiculo(int id, String patente, String marca, String modelo, int kilometraje) {
        this.Id = id;
        this.Patente = patente;
        this.Marca = marca;
        this.Modelo = modelo;
        this.Kilometraje = kilometraje;
    }

    public int getId() {
        return Id;
    }

    public String getPatente() {
        return Patente;
    }

    public String getMarca() {
        return Marca;
    }

    public String getModelo() {
        return Modelo;
    }

    public int getKilometraje() {
        return Kilometraje;
    }


}
