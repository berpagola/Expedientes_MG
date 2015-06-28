/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package conexiones;



import Clases.Estados;
import Clases.Expediente;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 *
 * @author martin
 */
public class HTMLData {
    
    
    public static final int EXP_ENCONTRADO=0;
    public static final int EXP_NO_ENCONTRADO=1;
    public static final int ERROR_PAGINA=2;
    public static final int SEGURIDAD= 1; 
    public static final int SALUD= 2;
    public static final int CONTADURIA= 3; 
    public static final int TESORERIA= 4; 
    private static final String URL_SEGURIDAD="http://sistemas.gba.gov.ar/consulta/expedientes/movimientos.php";
    private static final String URL_SALUD= "https://sistemas.ms.gba.gov.ar/expeconsulta/consulta_movimientos.php";
    private static final String URL_SALUD_TABLA= "https://sistemas.ms.gba.gov.ar/expeconsulta/tabla_movimientos.php";
    private static final String URL_CONTADURIA= "http://www.cgp.gba.gov.ar/Expedientes/Default.aspx";
    private static final String URL_TESORERIA= "http://www.tesoreria.gba.gov.ar/expedientes/resultado2.php";
    private static Map<String, String> params;
    
    public static int fetchData(Expediente exp, int pagina, Estados est){
        switch (pagina){
            case 1:
                return dataSeguridad(exp, est);
            case 2:
                return dataSalud(exp, est);
            case 3:
                return dataContaduria(exp, est);
            case 4:
                return dataTesoreria(exp, est);
            default: return -1;
        }
    }
    
    public static boolean checkPages(){
        try{
            params= new HashMap();
            params.put("caract", "1");
            params.put("nroexp", "1");
            params.put("anioexp", "14");
            params.put("alcance", "1");
            params.put("nrocuerpo", "1");
            params.put("ente", "1");
            params.put("nro", "1");
            params.put("anio", "14");
            params.put("alcance", "1");
            params.put("fue_migrado", "1");
            params.put("car", "1");
            params.put("alcance", "1");
            Jsoup.connect(URL_SEGURIDAD).timeout(20000).data(params).post();
            Document doc2= Jsoup.connect(URL_SALUD).timeout(20000).data(params).post();
            //System.out.println(doc2);
            try{
                String str= doc2.getElementById("formu").getElementsByClass("error").get(0).text();
                if (str.contains("Por el momento no puede mostrarse")) return false;
            }catch (Exception exception){}
            Jsoup.connect(URL_SALUD_TABLA).timeout(20000).data(params).get();
            Jsoup.connect(URL_CONTADURIA).timeout(20000).post();
            Jsoup.connect(URL_TESORERIA).timeout(20000).data(params).post();
            return true;
        } catch (Exception exception){
            return false;
        }
    }
            
    private static int dataSeguridad(Expediente exp, Estados estado){
        try{
            params= new HashMap();
            params.put("caract", exp.getCaratula());
            params.put("nroexp", exp.getNumero());
            params.put("anioexp", exp.getAnio());
            params.put("alcance", exp.getAlcance());
            params.put("nrocuerpo", "1");
            Document doc= Jsoup.connect(URL_SEGURIDAD).timeout(20000).data(params).post();
            Elements data= doc.getElementById("t_movimientos").getElementsByTag("tbody").get(1).getElementsByTag("tr");
            data= data.get(data.size()-1).getAllElements();
            StringTokenizer st = new StringTokenizer(data.get(3).text());
            estado.setFechaE(st.nextToken());
            estado.setFechaSal(estado.getFechaE());
            estado.setEstado(data.get(4).text());
            if (data.size()>5){
                st = new StringTokenizer(data.get(5).text());
                estado.setFechaR(st.nextToken());
            }
            estado.setPagina("Seguridad");
            return EXP_ENCONTRADO;
        } catch (UnknownHostException exception){
            System.out.println(exception);
            return ERROR_PAGINA; //No es posible acceder a la pagina
        } catch (Exception exception){
            System.out.println(exception);
            return EXP_NO_ENCONTRADO; //No se encontro el expediente
        }
        
    }
    
    private static int dataSalud(Expediente exp, Estados estado){
        params= new HashMap();
        params.put("ente", exp.getCaratula());
        params.put("nro", exp.getNumero());
        params.put("anio", exp.getAnio());
        params.put("alcance", exp.getAlcance());
        params.put("fue_migrado", "1");
        try{
            Document doc= Jsoup.connect(URL_SALUD).timeout(20000).data(params).post();           
            Elements data= doc.getElementById("formu").getElementsByTag("table").get(1).getElementsByClass("tabla2");
            StringTokenizer st;
            String type;
            for (Element aux:  data){
                st= new StringTokenizer(aux.text(), ":");
                type= st.nextToken();
                if (type.equals("Facturas")){
                    estado.setFactura(st.nextToken().substring(1));
                }
            }
            doc= Jsoup.connect(URL_SALUD_TABLA).data(params).timeout(20000).get();
            data= doc.getElementsByTag("table").get(0).getElementsByClass("columnaimpar");
            data= data.get(data.size()-1).getAllElements();
            estado.setFechaR(data.get(3).text());
            estado.setFechaE(data.get(2).text());
            estado.setFechaSal(estado.getFechaE());
            estado.setEstado(data.get(5).text());
            estado.setPagina("Salud");
            return EXP_ENCONTRADO;
        } catch (UnknownHostException exception){
            return ERROR_PAGINA; //No es posible acceder a la pagina
        } catch (Exception exception){
            try {
                Document doc2= Jsoup.connect(URL_SALUD_TABLA).timeout(20000).data(params).post();
                String aux = doc2.toString();
                //System.out.println(aux);
                if(aux.contains("El expediente buscado no posee movimientos"))
                   return EXP_NO_ENCONTRADO;
                if (aux.contains("El expediente no existe")) return EXP_NO_ENCONTRADO; //No es posible acceder a la pagina
                else return ERROR_PAGINA; //No se encontro el expediente
            } catch (Exception ex) {
                return ERROR_PAGINA; //No se encontro el expediente
            }
        }
        
    }
    
    private static int dataContaduria(Expediente exp, Estados estado){
        WebDriver driver= new HtmlUnitDriver();
        try{
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            driver.get(URL_CONTADURIA); 
            driver.findElement(By.id("ctl00_ContentPlaceHolder1_tbCaratula")).clear();
            driver.findElement(By.id("ctl00_ContentPlaceHolder1_tbCaratula")).sendKeys(exp.getCaratula());
            driver.findElement(By.id("ctl00_ContentPlaceHolder1_tbNumero")).clear();
            driver.findElement(By.id("ctl00_ContentPlaceHolder1_tbNumero")).sendKeys(exp.getNumero());
            driver.findElement(By.id("ctl00_ContentPlaceHolder1_tbAnio")).clear();
            driver.findElement(By.id("ctl00_ContentPlaceHolder1_tbAnio")).sendKeys(exp.getAnio());
            driver.findElement(By.id("ctl00_ContentPlaceHolder1_tbAlcance")).clear();
            driver.findElement(By.id("ctl00_ContentPlaceHolder1_tbAlcance")).sendKeys(exp.getAlcance());
            driver.findElement(By.id("ctl00_ContentPlaceHolder1_btnConsultar")).submit();
            driver.manage().timeouts().implicitlyWait(100, TimeUnit.NANOSECONDS);
            try{
                estado.setEstado(driver.findElement(By.id("lblJur")).getText());
                estado.setFechaCon(driver.findElement(By.id("lblEgreFecha")).getText());
                estado.setFechaE(estado.getFechaCon());
                estado.setPagina("");
                return EXP_ENCONTRADO;
            } catch (NoSuchElementException  e){
                try{ //el expediente esta en contaduria.
                     estado.setEstado(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblOficinaActual")).getText());
                     estado.setFechaR(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblFechaIngreso")).getText());
                     estado.setPagina("Contaduria");
                     return EXP_ENCONTRADO; 
                }catch(NoSuchElementException  ex){
                    return EXP_NO_ENCONTRADO; //No se encontro el expediente
                }
            } 
        }catch(Exception  ex){
            return ERROR_PAGINA; //No es posible acceder a la pagina
        }
        finally{
            driver.quit();
        }
    }
    
    private static int dataTesoreria(Expediente exp, Estados estado){
        try{
            params= new HashMap();
            params.put("car", exp.getCaratula());
            params.put("nro", exp.getNumero());
            params.put("anio", exp.getAnio().substring(2));
            params.put("alcance", exp.getAlcance());
            Document doc= Jsoup.connect(URL_TESORERIA).timeout(20000).data(params).post();
            String data= doc.getElementsByClass("conteiner-resultado2").get(0).text();
            if ((!data.contains("El Expediente no se encuentra")) && (data.indexOf("Expediente ")!=-1)){
                estado.setEstado(data.substring(data.indexOf("Expediente ")));
                estado.setPagina("Tesoreria");
                return EXP_ENCONTRADO;
            }else{
                return EXP_NO_ENCONTRADO;
            }
        } catch (UnknownHostException exception){
            return ERROR_PAGINA; //No es posible acceder a la pagina
        } catch (Exception exception){
            return EXP_NO_ENCONTRADO; //No se encontro el expediente
        }
        
    }
}
