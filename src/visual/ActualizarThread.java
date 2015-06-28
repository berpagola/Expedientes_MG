/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;

import Clases.Estados;
import Clases.Expediente;
import conexiones.HTMLData;
import conexiones.DBconection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author Martin
 */

public class ActualizarThread extends Thread {
    
    Actualizar paren;
    DBconection con;
    int cant;
    SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
    
    public ActualizarThread(String nro, Actualizar p){
        super(nro);
        try {
            con= DBconection.getDBConection();
            formateador = new SimpleDateFormat("dd/MM/yyyy");
            paren=p;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al conectarse con la Base de Datos. Intente establecer la conexion o contacte con el administrador");
            System.out.println(ex);
            System.exit(1);
        }
    }
    
    
    private int procesarSalud(Expediente expediente, Estados estado){
        int resultado= HTMLData.fetchData(expediente, HTMLData.SALUD, estado);
        switch (resultado){
            case HTMLData.EXP_ENCONTRADO: 
                if (!estado.getFactura().contains("$")) estado.setFactura("No figura la factura del expediente");
                if (estado.getEstado().contains("CONTADURIA GENERAL DE LA PROVINCIA")) estado.setPagina("Contaduria");
                break;
            case HTMLData.EXP_NO_ENCONTRADO:
                if(estado.getEstado().contains("No actualizado")){
                    estado.setEstado("EXPEDIENTE NO INGRESADO A MINISTERIOS");
                    estado.setFechaE("------");
                    estado.setFechaR("------");
                }
                break;
            case HTMLData.ERROR_PAGINA:
                pageError(expediente, "Salud");
                estado.copy(expediente.getEstado());
                break;
            default :
                break;
        }
        return resultado;
    }
    
    private int procesarSeguridad(Expediente expediente, Estados estado){
        int resultado= HTMLData.fetchData(expediente, HTMLData.SEGURIDAD, estado);
        switch (resultado){
            case HTMLData.EXP_ENCONTRADO: 
                if (!estado.getFactura().contains("$")) estado.setFactura("No figura la factura del expediente");
                if (estado.getEstado().contains("CONTADURIA GRAL PROVINCIA")) estado.setPagina("Contaduria");
                break;
            case HTMLData.EXP_NO_ENCONTRADO:
                estado.setEstado("EXPEDIENTE NO INGRESADO A MINISTERIOS");
                estado.setFechaE("------");
                estado.setFechaR("------");
                break;
            case HTMLData.ERROR_PAGINA:
                pageError(expediente, "Seguridad");
                estado.copy(expediente.getEstado());
                break;
            default :
                break;
        }
        return resultado;
    }
    
    private int procesarContaduria(Expediente expediente, Estados estado){
        try{
        int resultado= HTMLData.fetchData(expediente, HTMLData.CONTADURIA, estado);
        switch (resultado){
            case HTMLData.EXP_ENCONTRADO: 
                if (estado.getPagina().isEmpty()){
                    estado.setFechaR("------");
                    Date fsalud= formateador.parse(estado.getFechaSal());
                    Date fcont= formateador.parse(estado.getFechaCon());
                    if (estado.getEstado().contains("TGP") && fsalud.before(fcont)){
                        estado.setEstado("TESORERIA GENERAL DE LA PROVINCIA");
                        estado.setPagina("Tesoreria");
                    }else if(estado.getEstado().contains("MRIO.SALUD") && fsalud.before(fcont)){
                        estado.setPagina("Salud");
                        estado.setEstado("MINISTERIO DE SALUD DE LA PROVINCIA");
                    }else if(estado.getEstado().contains("MRIO.SALUD") && !fsalud.before(fcont)){
                        estado.setPagina("Salud");
                        estado.setEstado("CONTADURIA GRAL PROVINCIA");
                    }
                    else if(estado.getEstado().contains("SEGURIDAD") && fsalud.before(fcont)){
                        estado.setPagina("Salud");
                        estado.setEstado("SEGURIDAD");
                    } else 
                        estado.setPagina("Contaduria");  
                }
                break;
            case HTMLData.EXP_NO_ENCONTRADO:
                estado.setPagina("Contaduria"); 
                break;
            case HTMLData.ERROR_PAGINA:
                pageError(expediente, "Contaduria");
                estado.copy(expediente.getEstado());
                break;
            default :
                break;
        }
        return resultado;
        }catch (ParseException exception){
            JOptionPane.showMessageDialog(null, "Se produjo un error de formato del tipo Fecha en el expediente "+expediente.toFormatedString()+", contacte con el desarrollador");  
            System.exit(0);
            return 0;
        }
    }
    
    private int procesarTesoreria(Expediente expediente, Estados estado){
        int resultado= HTMLData.fetchData(expediente, HTMLData.TESORERIA, estado);
        estado.setFechaR("------");
        switch (resultado){
            case HTMLData.EXP_ENCONTRADO:
                estado.setEstado(estado.getEstado().substring(11));
                break;
            case HTMLData.EXP_NO_ENCONTRADO:
                estado.setFechaE("------");
                if (expediente.getCaratula().equals("21200"))
                    estado.setPagina("Seguridad");
                else
                    estado.setPagina("Salud");
                break;
            case HTMLData.ERROR_PAGINA:
                pageError(expediente, "Tesoreria");
                estado.copy(expediente.getEstado());
                break;
            default :
                break;
        }
        return resultado;
    }
        
    //comienza la actualizacion de los expedientes
    @Override
    public void run(){
        try {      
            con= DBconection.getDBConection();
            ArrayList<Expediente> expedientes= con.getExpedientes(); //me traigo todos los expedientes de la base de datos
            Estados estado;
            int result=0;
            cant=0;
            if (!HTMLData.checkPages()) 
                JOptionPane.showMessageDialog(null, "Una o varias paginas web de los ministerios no se encuentran funcionando correctamente. Intente actualizar mas tarde");
            else{
                for (int i=0; i<expedientes.size(); i++){
                    estado= new Estados();
                    estado.copy(expedientes.get(i).getEstado());
                    if (estado.getPagina().equals("Seguridad")){
                        result=procesarSeguridad(expedientes.get(i), estado);
                    }
                    if (estado.getPagina().equals("Salud")){
                        result=procesarSalud(expedientes.get(i), estado);
                    }
                    if (estado.getPagina().equals("Contaduria")){
                        result=procesarContaduria(expedientes.get(i), estado);
                        if (estado.getPagina().equals("Seguridad")){
                            result=procesarSeguridad(expedientes.get(i), estado);
                        }
                        else if (estado.getPagina().equals("Salud")){
                            result=procesarSalud(expedientes.get(i), estado);
                        }
                    }
                    if (estado.getPagina().equals("Tesoreria")){
                        result=procesarTesoreria(expedientes.get(i), estado);
                        if (estado.getPagina().equals("Seguridad")){
                            result=procesarSeguridad(expedientes.get(i), estado);
                            if (estado.getPagina().equals("Contaduria")){
                                result=procesarContaduria(expedientes.get(i), estado);
                             /*   if (estado.getPagina().equals("Tesoreria")){
                                    procesarTesoreria(expedientes.get(i), estado);
                                    estado.setPagina("Tesoreria");
                                } */
                            }
                        }
                        else if (estado.getPagina().equals("Salud")){
                            result=procesarSalud(expedientes.get(i), estado);
                            if (estado.getPagina().equals("Contaduria")){
                                result=procesarContaduria(expedientes.get(i), estado);
                              /*  if (estado.getPagina().equals("Tesoreria")){
                                    procesarTesoreria(expedientes.get(i), estado);
                                    estado.setPagina("Tesoreria");
                                } */
                            }
                        }
                    }
                    if (result!=HTMLData.ERROR_PAGINA) con.updateExpediente(expedientes.get(i), estado);
                    else{
                        JOptionPane.showMessageDialog(null, "El expediente "+ expedientes.get(i).getCaratula()+"-"+expedientes.get(i).getNumero()+"/"+expedientes.get(i).getAnio()+"-"+expedientes.get(i).getAlcance()+" no fue actualizado"); 
                    }
                    cant++;
                    paren.getbarra().setValue((int)((cant*100)/expedientes.size()));
                    paren.getporce().setText(String.valueOf(paren.getbarra().getValue())+ "%");
                    paren.repaint();
                    if (paren.cancelar && cancel()) return;
                }
                if (cant==expedientes.size()) JOptionPane.showMessageDialog(null, "Se actualizaron los expedientes correctamente");
            }
            paren.parent.setEnabled(true);
            paren.parent.setVisible(true);
            paren.dispose();
            
        }
         catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Se actualizaron "+cant+ " expedientes. Error al conectarse con la Base de Datos. Intente establecer la conexion o contacte con el administrador");
            System.exit(0);
        }
    }
    
    private boolean cancel(){
        int seleccion = JOptionPane.showOptionDialog(
            paren, // Componente padre
            "¿Esta seguro que desea detener la actualizacion? Los datos procesados hasta este punto se mantendran actualizados", //Mensaje
            "Advertencia", // Título
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,    // null para icono por defecto.
            new Object[] { "Si", "No"},// null para YES, NO y CANCEL
            "Si"
        );
        if(seleccion==0){
            JOptionPane.showMessageDialog(null, "Se actualizaron "+cant+ " expedientes");
            paren.parent.setEnabled(true);
            paren.parent.setVisible(true);
            paren.dispose();
            return true;
        }else{
            paren.cancelar=false;
            return false;
        }
    }
    
    public boolean pageError(Expediente ex, String pagina){
        int seleccion = JOptionPane.showOptionDialog(
            paren, // Componente padre
            "El expediente "+ex.toFormatedString()+" no fue actualizado porque la pagina de "+pagina+" No se encuentra funcionando. ¿Desea continuar con la actualizacion?", //Mensaje
            "Advertencia", // Título
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,    // null para icono por defecto.
            new Object[] { "Si", "No"},// null para YES, NO y CANCEL
            "Si"
        );
        if(seleccion==0){
            return false;
        }else{
            JOptionPane.showMessageDialog(null, "Se actualizaron "+cant+ " expedientes");
            paren.parent.setEnabled(true);
            paren.parent.setVisible(true);
            paren.dispose();
            return true;
        }
    }
    

}
