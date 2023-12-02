package com.example.flotacolectivos;
import java.io.Serializable;
public class AuntenticarUsuario implements Serializable {
    private String email;
    private String contrasena;


    public AuntenticarUsuario(String email, String contrasena) {
        this.email = email;
        this.contrasena = contrasena;

    }

    public String getEmail() {
        return email;
    }
}