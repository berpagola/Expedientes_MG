/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package conexiones;

import Clases.Estados;
import Clases.Expediente;
import Clases.Hospital;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author martin
 */


public class DBconection {
   
    private Connection conection = null;
    
    private static DBconection conexion;
    
    private DBconection() throws SQLException{
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            conection=DriverManager.getConnection("jdbc:derby:.\\DB\\MGinsumosDB;");
        }catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "No se ha podido detectar el driver de conexion a base de datos. Contactarse con el administrador");
            System.exit(0);
        } catch (SQLException ex) {
            conection=DriverManager.getConnection("jdbc:derby:.\\DB\\MGinsumosDB;create=true");
            PreparedStatement ps= conection.prepareStatement(dataSQLestados);
            ps.execute();
            ps= conection.prepareStatement(dataSQLexpedientes);
            ps.execute();
            ps= conection.prepareStatement(dataSQLhospitales);
            ps.execute();
            ps.close();
        }
    }
    
    public static DBconection getDBConection() throws SQLException{
        if (conexion==null) return conexion=new DBconection();
        else return conexion;
    }
    
    
    public void desconectar() throws SQLException{
        conection.close();
    }
    
    public int cantExpedientes() throws SQLException{
        Statement st= this.conection.createStatement();
        ResultSet rs= st.executeQuery("SELECT COUNT(*) AS Cantidad FROM Expedientes");
        rs.next();
        st.close();
        return Integer.valueOf(rs.getString("Cantidad"));
    }
    
    public ArrayList<Expediente> getExpedientes() throws SQLException{
        ArrayList<Expediente> expedientes= new ArrayList<Expediente>();
        Statement st= this.conection.createStatement();
        ResultSet rs= st.executeQuery("SELECT * FROM Expedientes Inner Join Estados ON (Expedientes.Expediente=Estados.Expediente)");
        String car;
        String nro;
        String ano;
        String alc;
        Estados state;
        while (rs.next()){
            car=rs.getString("Caratula");
            nro=rs.getString("Numero");
            ano=rs.getString("Ano");
            alc=rs.getString("Alcance");
            state= new Estados();
            state.setFactura(rs.getString("Factura"));
            state.setPagina(rs.getString("Pagina"));
            state.setEstado(rs.getString("Estado"));
            state.setFechaE(rs.getString("FechaEnv"));
            state.setFechaR(rs.getString("FechaRec"));
            state.setFechaSal(rs.getString("FechaSalud"));
            state.setFechaCon(rs.getString("FechaContaduria"));
            state.setActualizado(rs.getString("Actualizado"));
            expedientes.add(new Expediente(car, nro, ano, alc, state));
        }
        st.close();
        rs.close();
        return expedientes; 
    }
    
    public ArrayList<Expediente> getExpedientes(String key, String value) throws SQLException{
        ArrayList<Expediente> expedientes= new ArrayList<Expediente>();
        if (key.equals("Caratula") | key.equals("Numero") | key.equals("Anio") | key.equals("Alcance")) key= "Expedientes."+key;
        else key= "Estados."+key;
        Statement st= this.conection.createStatement();
        ResultSet rs=st.executeQuery("SELECT * FROM Expedientes Inner Join Estados ON (Expedientes.Expediente=Estados.Expediente) WHERE "+key+" ='"+value+"'");
        String car;
        String nro;
        String ano;
        String alc;
        Estados state;
        while (rs.next()){
            car=rs.getString("Caratula");
            nro=rs.getString("Numero");
            ano=rs.getString("Ano");
            alc=rs.getString("Alcance");
            state= new Estados();
            state.setFactura(rs.getString("Factura"));
            state.setPagina(rs.getString("Pagina"));
            state.setEstado(rs.getString("Estado"));
            state.setFechaE(rs.getString("FechaEnv"));
            state.setFechaR(rs.getString("FechaRec"));
            state.setFechaSal(rs.getString("FechaSalud"));
            state.setFechaCon(rs.getString("FechaContaduria"));
            state.setActualizado(rs.getString("Actualizado"));
            expedientes.add(new Expediente(car, nro, ano, alc, state));
        }
        st.close();
        rs.close();
        return expedientes; 
    }
    
    public void updateExpediente(Expediente expediente, Estados estado) throws SQLException{
        Statement ac= this.conection.createStatement();
        char[] cas=estado.getEstado().toLowerCase().toCharArray();
        cas[0]=Character.toUpperCase(cas[0]);
        estado.setEstado(String.valueOf(cas));
        estado.setEstado(estado.getEstado().replace((char)65533, 'ñ'));
        if (!expediente.getEstado().getEstado().equals(estado.getEstado()) || !expediente.getEstado().getFechaR().equals(estado.getFechaR())){
            ac.executeUpdate("UPDATE Estados SET Expediente='"+expediente.toString()+"', FechaRec='"+estado.getFechaR()+"', Factura='"+estado.getFactura()+"', FechaEnv='"+estado.getFechaE()+"', FechaSalud='"+estado.getFechaSal()+"', FechaContaduria='"+estado.getFechaCon()+"', Estado='"+estado.getEstado()+"', Pagina='"+estado.getPagina()+"', Actualizado='Si' WHERE Expediente='"+expediente.toString()+"'");       
        }else ac.executeUpdate("UPDATE Estados SET Pagina='"+estado.getPagina()+"', Actualizado='No' WHERE Expediente='"+expediente.toString()+"'");
    }
    
    public boolean addExpediente(Expediente expediente) throws SQLException{
        Statement st= this.conection.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Expedientes WHERE Expediente = '"+expediente.toString()+"'");
        if (!rs.next()){
            String pagina;
            if (expediente.getCaratula().equals("21200")) pagina= "Seguridad";
            else pagina="Salud";
            st.executeUpdate("INSERT INTO Expedientes(Expediente, Caratula, Numero, Ano, Alcance) VALUES ('"+expediente.toString()+"', '"+expediente.getCaratula()+"', '"+expediente.getNumero()+"', '"+expediente.getAnio()+"', '"+expediente.getAlcance()+"')");
            st.executeUpdate("INSERT INTO Estados(Expediente, FechaSalud, FechaContaduria, FechaEnv, Estado, FechaRec, Factura, Pagina, Actualizado) VALUES ('"+expediente.toString()+"', '01/01/1999', '01/01/2000', '------', 'No actualizado','------', '------', '"+pagina+"', 'No')");  
            st.close();
            rs.close();
            return true;
        }else{
            st.close();
            rs.close();
            return false;
        }
    }
    
    public boolean deleteExpediente(Expediente expediente) throws SQLException{
        Statement st= this.conection.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Expedientes WHERE Expediente = '"+expediente.toString()+"'");
        if (rs.next()){
            st.executeUpdate("DELETE FROM  Expedientes WHERE  Expediente ='"+expediente.toString()+"'");
            st.executeUpdate("DELETE FROM  Estados WHERE  Expediente ='"+expediente.toString()+"'");
            st.close();
            rs.close();
            return true;
        }else{
            st.close();
            rs.close();
            return false;
        }
    }
    
    public boolean deleteExpediente(String expediente) throws SQLException{
        Statement st= this.conection.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM Expedientes WHERE Expediente = '"+expediente+"'");
        if (rs.next()){
            st.executeUpdate("DELETE FROM  Expedientes WHERE  Expediente ='"+expediente+"'");
            st.executeUpdate("DELETE FROM  Estados WHERE  Expediente ='"+expediente+"'");
            st.close();
            rs.close();
            return true;
        }else{
            st.close();
            rs.close();
            return false;
        }
    }
    
    public ArrayList<Hospital> getHospitales(String key, String value) throws SQLException{
        ArrayList<Hospital> hospitales= new ArrayList<Hospital>();
        Statement st= this.conection.createStatement();
        ResultSet rs=st.executeQuery("SELECT * FROM hospitales WHERE "+key+" ='"+value+"'");
        while (rs.next()){
            hospitales.add(new Hospital(rs.getString("hospital"), rs.getString("caratula")));
        }
        st.close();
        rs.close();
        return hospitales;
    }
    
    public ArrayList<Hospital> getHospitales() throws SQLException{
        ArrayList<Hospital> hospitales= new ArrayList<Hospital>();
        Statement st= this.conection.createStatement();
        ResultSet rs=st.executeQuery("SELECT * FROM hospitales");
        while (rs.next()){
            hospitales.add(new Hospital(rs.getString("hospital"), rs.getString("caratula")));
        }
        st.close();
        rs.close();
        return hospitales;
    }
    
    public boolean addHospital(Hospital hospital) throws SQLException{
        Statement st= this.conection.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM hospitales WHERE caratula ='"+hospital.getCaratula()+"'");
        if (!rs.next()){
            st.executeUpdate("INSERT INTO hospitales(hospital, caratula) VALUES ('"+hospital.getNombre()+"', '"+hospital.getCaratula()+"')");
            st.close();
            rs.close();
            return true;
        }else{
            st.close();
            rs.close();
            return false;
        }
    }
    
    public boolean deleteHospital(Hospital hospital) throws SQLException{
        Statement st= this.conection.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM hospitales WHERE caratula ='"+hospital.getCaratula()+"'");
        if (rs.next()){
            st.executeUpdate("DELETE FROM hospitales WHERE caratula ='"+hospital.getCaratula()+"'");
            st.close();
            rs.close();
            return true;
        }else{
            st.close();
            rs.close();
            return false;
        }
    }
    
    private final String dataSQLestados= "CREATE TABLE estados (\n" +
"  Expediente varchar(20) NOT NULL,\n" +
"  FechaEnv varchar(20) DEFAULT NULL,\n" +
"  FechaRec varchar(20) DEFAULT NULL,\n" +
"  Estado varchar(255) DEFAULT NULL,\n" +
"  Factura varchar(255) DEFAULT NULL,\n" +
"  Actualizado varchar(45) DEFAULT 'No',\n" +
"  FechaSalud varchar(45) DEFAULT '0',\n" +
"  FechaContaduria varchar(45) DEFAULT NULL,\n" +
"  Pagina varchar(45) DEFAULT NULL,\n" +
"  PRIMARY KEY (Expediente)\n" +
")\n";
 private final String dataSQLexpedientes="CREATE TABLE expedientes (\n" +
"  Expediente varchar(45) NOT NULL,\n" +
"  Caratula varchar(45) DEFAULT NULL,\n" +
"  Numero varchar(45) DEFAULT NULL,\n" +
"  Ano varchar(45) DEFAULT NULL,\n" +
"  Alcance varchar(45) DEFAULT NULL,\n" +
"  PRIMARY KEY (Expediente)\n" +
")\n";
private final String dataSQLhospitales="CREATE TABLE hospitales\n" +
"(\n" +
"idhospitales INT not null\n" +
"        GENERATED ALWAYS AS IDENTITY\n" +
"        (START WITH 1, INCREMENT BY 1),   \n" +
"  hospital varchar(45) DEFAULT NULL,\n" +
"  caratula varchar(45) DEFAULT NULL,\n" +
"  PRIMARY KEY (idhospitales) \n" +
")\n" +
"";
}
