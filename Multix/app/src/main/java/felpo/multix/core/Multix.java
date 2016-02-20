package felpo.multix.core;

import java.util.*;
import java.net.*;
import java.io.*;

import felpo.multix.android.ATool;
import felpo.tools.*;


public class Multix {
    private static final String DF_WEB_PAGE_URL = "http://www.finanzas.df.gob.mx/sma/detallePlaca.php?placa=";
    private static final String SIN_ADEUDOS = "sin adeudos!";
    private static final String FOLIO = "folio";
    public  static final String PAGADA = "pagada";
    public static final int MIN_PLACA_SIZE = 5;
    public static final int MAX_PLACA_SIZE = 10;
    public static final int SALARIO_MINIMO = 73;


    public static int count=0;


    public static History requestHistory(String placa) throws IOException{
        String m = requestHtml(DF_WEB_PAGE_URL+placa);
        m = extractPlainText(m);
        m = extractMainInfo(m);
        List<Multa> multas = extractMultas(m);     //if(count==0){ multas.remove(0); count=1; }
        History h = new History(placa, multas);
        return h;
    }

    public static List<Multa> extractDifferences(History old, History history) throws Exception{
        List<Multa> dif = new ArrayList<Multa>();
        if(!old.placa.equals(history.placa))
            throw new Exception("Las Placas no son iguales");

        Collections.sort(old.multas);
        Collections.sort(history.multas);

        int so = old.multas.size();
        int sn = history.multas.size();
        int d = sn - so;

        if(d>0)
            dif = history.multas.subList(sn-d, sn);
        return dif;
    }

    public static boolean isSancionConvertibleToPesos(String sancion){
        Scanner sc = new Scanner(sancion);
        return sc.hasNextInt();
    }

    public static String calculateSancionInPesos(String sancion){
        Scanner sc = new Scanner(sancion);
        int s = sc.nextInt();
        s *= SALARIO_MINIMO;
        return "$"+s+".00";
    }

    public static String requestHistoryInString(String placa) throws IOException {
        String m = requestHtml(DF_WEB_PAGE_URL+placa);
        m = extractPlainText(m);
        return extractMainInfo(m);
    }

    public static String requestHtml(String url_string) throws MalformedURLException, IOException {
        URL url = new URL(url_string);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setConnectTimeout((int)Tool.SECOND*4);
        httpConnection.setReadTimeout((int)Tool.SECOND*3);
        httpConnection.connect();
        InputStream in = httpConnection.getInputStream();
        String r = new String(Tool.extract(in));
        in.close();
        httpConnection.disconnect();
        return r;
    }

    public static String extractPlainText(String html){
        html = html.replaceAll("<[^>]*>", " ");
        html = html.toLowerCase();
        html = html.replace("&aacute;", "a");
        html = html.replace("&eacute;", "e");
        html = html.replace("&iacute;", "i");
        html = html.replace("&oacute;", "o");
        html = html.replace("&uacute;", "u");
        html = html.replace("&ntilde;","ñ");
        return html;
    }

    public static String extractMainInfo(String plain_text){
        int a = plain_text.indexOf("placa:"  );
        int b = plain_text.indexOf("si usted");
        plain_text = plain_text.substring(a,b);
        return plain_text;
    }

    public static String extractPlaca(String main_info){
        Scanner sc = new Scanner(main_info);
        sc.next();
        String s = sc.next();
        sc.close();
        return s;
    }

    public static List<Multa> extractMultas(String main_info){
        List<Multa> multas = new ArrayList<Multa>();
        if(main_info.contains(SIN_ADEUDOS))
            return multas;

        Scanner sc = new Scanner(main_info);
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
                if(status.equals("no")){
                    status += " "+sc.next();
                    sc.next();
                    sc.next();
                }
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

    public static boolean validatePlaca(String placa){
        return placa.length() >= MIN_PLACA_SIZE && placa.length()<= MAX_PLACA_SIZE && placa.matches("\\w*");
    }

}