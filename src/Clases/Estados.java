/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

/** 
 *
 * @author Martin
 */
public class Estados {
    
    private String fechaRecivido; // fecha recivido
    private String fechaEnvio; //fecha de envio
    private String estado; 
    private String factura;
    private String pagina;
    private String fechaSal; 
    private String fechaCon;
    private String actualizado;


    public Estados() {
        this.fechaRecivido = new String();
        this.fechaEnvio = new String();
        this.estado = new String();
        this.factura = new String();
        this.pagina = new String();
        this.actualizado= new String("No");
        this.fechaSal = new String("01/01/1999");
        this.fechaCon = new String("01/01/2000");
    }


    public void clear(){
        this.fechaRecivido = new String();
        this.fechaEnvio = new String();
        this.estado = new String();
        this.factura = new String();
        this.pagina = new String();
        this.actualizado= new String("No");
        this.fechaSal = new String("01/01/1999");
        this.fechaCon = new String("01/01/2000");
    }
    
    public void copy(Estados es){
        this.fechaRecivido = new String(es.getFechaR());
        this.fechaEnvio = new String(es.getFechaE());
        this.estado = new String(es.getEstado());
        this.factura = new String(es.getFactura());
        this.pagina = new String(es.getPagina());
        this.fechaSal= new String(es.getFechaSal());
        this.fechaCon= new String(es.getFechaCon());
        this.actualizado= new String(es.getActualizado());
        
    }
    
    public String getActualizado() {
        return actualizado;
    }

    public String getFechaSal() {
        return fechaSal;
    }

    public String getFechaCon() {
        return fechaCon;
    }

    public void setFechaSal(String fechaSal) {
        this.fechaSal = new String(fechaSal);
    }

    public void setFechaCon(String fechaCon) {
        this.fechaCon = new String(fechaCon);
    }

    public String getPagina() {
        return pagina;
    }

    public void setPagina(String pagina) {
        this.pagina = new String(pagina);
    }

    public String getFechaR() {
        return fechaRecivido;
    }

    public String getFechaE() {
        return fechaEnvio;
    }

    public String getEstado() {
        return estado;
    }

    public String getFactura() {
        return factura;
    }

    public void setFechaR(String fechaR) {
        this.fechaRecivido = new String(fechaR);
    }

    public void setFechaE(String fechaE) {
        this.fechaEnvio = new String(fechaE);
    }

    public void setEstado(String estado) {
        this.estado = new String(estado);
    }

    public void setFactura(String factura) {
        this.factura = new String(factura);
    }
    
    public void setActualizado(String actualizado) {
        this.actualizado = actualizado;
    }
    
    
}
