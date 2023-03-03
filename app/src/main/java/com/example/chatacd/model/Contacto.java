package com.example.chatacd.model;

/**
 * <h1>Contacto</h1>
 *
 *  La clase Contacto permitira crear los contactos con distintos atributos
 */

public class Contacto {

    //Declaracion de atributos
    private String nombre;
    private String ultimoMensaje;
    private String img;
    private String ip;

    /**
     * Cosntructor de la clase Contacto
     *
     * @param ip            Numero de ip del contacto
     * @param nombre        Nombre del contacto
     * @param ultimoMensaje Ultimo mensaje del contacto
     * @param img           Url de la imagen del contacto
     */
    public Contacto(String ip, String nombre, String ultimoMensaje, String img) {
        this.ip = ip;
        this.nombre = nombre;
        this.ultimoMensaje = ultimoMensaje;
        this.img = img;
    }

    /**
     * Getters y seters de la clase Contacto
     */

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

    /**
     * <h1>toString</h1>
     *
     * Este metodo permite convertir un objeto Contacto a string para mostrar todos sus datos
     * @return String con los datos del contacto
     */
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
