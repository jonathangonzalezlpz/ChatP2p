package sample.Model;

import sample.RMI.Client;

import java.io.Serializable;

public class Mensaje implements Serializable {
    private Client autor;
    private String contenido;
    private Boolean recibido;

    //Constructores
    public Mensaje(Client autor, String contenido){
        this.autor = autor;
        this.contenido = contenido;
        this.recibido = false;
    }

    public Mensaje(Client autor , String contenido, Boolean recibido){
        this.autor = autor;
        this.contenido = contenido;
        this.recibido = recibido;
    }

    //Getters y Setters

    public Client getAutor() {
        return autor;
    }

    public void setAutor(Client autor) {
        this.autor = autor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Boolean getRecibido() {
        return recibido;
    }

    public void setRecibido(Boolean recibido) {
        this.recibido = recibido;
    }

}
