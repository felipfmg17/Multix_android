package felpo.multix.core;

import java.util.*;
import java.net.*;
import java.io.*;
import felpo.tools.*;


public class Multix {
    private static final String DF_WEB_PAGE_URL = "http://www.finanzas.df.gob.mx/sma/detallePlaca.php?placa=";
    private static final String SIN_ADEUDOS = "sin adeudos!";
    private static final String FOLIO = "folio";
    public  static final String PAGADA = "pagada";
    
    
    public static History requestHistory(String placa) throws IOException{
        String m = requestHtml(DF_WEB_PAGE_URL+placa);
        m = extractPlainText(m);
        m = extractMainInfo(m);
        List<Multa> multas = extractMultas(m);
        History h = new History(placa, multas);
        return h;
    }
    
    public static List<Multa> extractDifferences(History old, History neww) throws Exception{
        List<Multa> dif = new ArrayList<Multa>();
        if(!old.placa.equals(neww.placa))
            throw new Exception("Las Placas no son iguales");

        Collections.sort(old.multas);
        Collections.sort(neww.multas);

        int so = old.multas.size();
        int sn = neww.multas.size();
        int d = sn - so;
        
        if(d>0)
            dif = neww.multas.subList(sn-d, sn);
        return dif;
    }
    
    
    private static String requestHtml(String m) throws MalformedURLException, IOException {
        URL url = new URL(m);
        URLConnection con = url.openConnection();
        con.connect();
        long size = con.getContentLength();
        InputStream in = con.getInputStream();
        String r = new String(Tool.extract(in, size));
        return r;
    }
    
    private static String extractPlainText(String m){
        m = m.replaceAll("<[^>]*>", " ");
        m = m.toLowerCase();
        m = m.replace("&aacute;", "a");
        m = m.replace("&eacute;", "e");
        m = m.replace("&iacute;", "i");
        m = m.replace("&oacute;", "o");
        m = m.replace("&uacute;", "u");
        m = m.replace("&ntilde;","ñ");
        return m;
    }
    
    private static String extractMainInfo(String m){
        int a = m.indexOf("placa:"  );
        int b = m.indexOf("si usted");
        m = m.substring(a,b);
        return m;
    }
    
    private static String extractPlaca(String m){
        Scanner sc = new Scanner(m);
        sc.next();
        String s = sc.next();
        sc.close();
        return s;
    }
    
    private static List<Multa> extractMultas(String m){
        List<Multa> multas = new ArrayList<Multa>();
        if(m.contains(SIN_ADEUDOS)) 
            return multas;
        
        Scanner sc = new Scanner(m);
        sc.nextLine();
        
        String folio;
        String fecha;
        String sancion;
        String motivo;
        String status;
        
        while(sc.hasNext()){
            String s = sc.next();
            if(s.equals(FOLIO)){
                sc.nextLine();
                folio = sc.next();
                fecha = sc.next();
                status = sc.next();
                sc.next();
                motivo = sc.nextLine().trim();
                sc.nextLine();
                sc.next();
                sancion = sc.nextLine().trim();
                Multa multa = new Multa(folio, fecha, sancion, motivo, status);
                multas.add(multa);
            }
            else
                break;
        }
        
        return multas;
    }
}
