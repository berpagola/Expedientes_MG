/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package visual;

import Clases.Expediente;
import Clases.Hospital;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author martin
 */
public class ViewModel {
    
    public static DefaultTableModel setExpedientesTableModel(JTable tabla, ArrayList<Expediente> expedientes){
        DefaultTableModel modelo= new DefaultTableModel() {
            //Con esto conseguimos que la tabla no se pueda editar
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false; 
            }
         };
         modelo.addColumn("Modificado");
         modelo.addColumn("Expediente");
         modelo.addColumn("Fecha de Envío");
         modelo.addColumn("Fecha de Recepción");
         modelo.addColumn("Estado");
         modelo.addColumn("Factura");
        for (int i=0; i<expedientes.size(); i++){
            modelo.addRow(expedientes.get(i).toObjectArray()); // Añade una fila al final del modelo de la tabla 
        }
        tabla.setModel(modelo);
        tabla.updateUI();
        tabla.getColumnModel().getColumn(0).setPreferredWidth(20);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);
       // tabla.getColumnModel().getColumn(0).setResizable(false);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(90);
      //  tabla.getColumnModel().getColumn(1).setResizable(false);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(90);
       // tabla.getColumnModel().getColumn(2).setResizable(false);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(350);
      //  tabla.getColumnModel().getColumn(3).setResizable(false);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(350);
        //Actualiza la tabla
        tabla.setRowHeight(25);
        TableRowSorter<TableModel> ordenar = new TableRowSorter<TableModel>(modelo);
        tabla.setRowSorter(ordenar);
        return modelo;
    }
    
    //public static DefaultTableModel getModel(ArrayList<Expediente> expedientes)
    
    public static DefaultTableModel setHospitalesTableModel(JTable tabla, ArrayList<Hospital> hospitales){
        DefaultTableModel modelo= new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                    return false; //Con esto conseguimos que la tabla no se pueda editar
                }
            };
             modelo.addColumn("Hospital");
             modelo.addColumn("Caratula");
            for (int i=0; i<hospitales.size(); i++){
                modelo.addRow(hospitales.get(i).toObjectArray()); // Añade una fila al final del modelo de la tabla 
            }
            tabla.setModel(modelo);
            tabla.updateUI();
            tabla.getColumnModel().getColumn(0).setPreferredWidth(100);
           // tabla.getColumnModel().getColumn(0).setResizable(false);
            tabla.getColumnModel().getColumn(1).setPreferredWidth(90);
          //  tabla.getColumnModel().getColumn(1).setResizable(false);
            //Actualiza la tabla
            tabla.setRowHeight(25); 
            return modelo;
    }
    
}
