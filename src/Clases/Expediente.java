/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Clases;

import java.sql.ResultSet;
import java.util.StringTokenizer;

/**
 *
 * @author martin
 */
public class Expediente {
    
    private String caratula;
    private String numero;
    private String anio;
    private String alcance;
    private Estados estado;

    public String getCaratula() {
        return caratula;
    }

    public String getNumero() {
        return numero;
    }

    public String getAnio() {
        return anio;
    }

    public String getAlcance() {
        return alcance;
    }

    public Estados getEstado() {
        return estado;
    }

    public void setCaratula(String caratula) {
        this.caratula = caratula;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
    }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }

    public Expediente(String caratula, String numero, String anio, String alcance) {
        this.caratula = caratula;
        this.numero = numero;
        this.anio = anio;
        this.alcance = alcance;
        this.estado= new Estados();
    }
    
    public Expediente(String expediente) {
        StringTokenizer st= new StringTokenizer(expediente, "-/");
        this.caratula = st.nextToken();
        this.numero = st.nextToken();
        this.anio = st.nextToken();
        this.alcance = st.nextToken();
        this.estado= new Estados();
    }
    
    public Expediente(String caratula, String numero, String anio, String alcance, Estados est) {
        this.caratula = caratula;
        this.numero = numero;
        this.anio = anio;
        this.alcance = alcance;
        this.estado= new Estados();
        this.estado.copy(est);
    }

    @Override
    public String toString() {
        return caratula+numero+anio+alcance;
    }
    
    public String toFormatedString(){
        return caratula+"-"+numero+"/"+anio+"-"+alcance;
    }
    
    public static String toUnformatedString(String expediente){
        String out=new String(expediente.replace("-", ""));
        out=out.replace("/", "");
        return out;
    }
    
    public Object[] toObjectArray(){
        Object[] fila = new Object[6];
        fila[0] = this.estado.getActualizado();
        fila[1] = this.toFormatedString();
        if(!this.estado.getFechaE().equals("1900-01-01")) fila[2] = this.estado.getFechaE();
        if(!this.estado.getFechaR().equals("1900-01-01")) fila[3] = this.estado.getFechaR();
        fila[4] = this.estado.getEstado();
        fila[5] = this.estado.getFactura();
        return fila;
    }
    
    
    
    
    
}
