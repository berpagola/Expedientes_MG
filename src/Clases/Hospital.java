/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Clases;

/**
 *
 * @author martin
 */
public class Hospital {
    
    private String nombre;
    private String caratula;

    public Hospital(String nombre, String caratula) {
        this.nombre = nombre;
        this.caratula = caratula;
    }
    
    public Hospital(String caratula) {
        this.nombre = new String();
        this.caratula = caratula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCaratula() {
        return caratula;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCaratula(String caratula) {
        this.caratula = caratula;
    }
    
    public Object[] toObjectArray(){
        Object[] fila = new Object[2];
        fila[0] = this.nombre;
        fila[1] = this.caratula;
        return fila;
    }
    
    
}
