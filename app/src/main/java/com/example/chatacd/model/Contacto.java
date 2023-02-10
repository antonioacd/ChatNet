package com.example.chatacd.model;

public class Contacto {

    private String nombre;
    private String ultimoMensaje;
    private String img;
    private String ip;

    public Contacto(String ip, String nombre, String ultimoMensaje, String img) {
        this.ip = ip;
        this.nombre = nombre;
        this.ultimoMensaje = ultimoMensaje;
        this.img = img;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "nombre='" + nombre + '\'' +
                ", ultimoMensaje='" + ultimoMensaje + '\'' +
                ", img='" + img + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
