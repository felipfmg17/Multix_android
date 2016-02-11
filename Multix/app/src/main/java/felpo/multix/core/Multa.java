package felpo.multix.core;

import java.io.*;

public class Multa implements Comparable<Multa>, Serializable{
    public String folio;
    public String fecha;
    public String sancion;
    public String motivo;
    public String status;

    public Multa(String folio, String fecha, String sancion, String motivo, String status) {
        this.folio = folio;
        this.fecha = fecha;
        this.sancion = sancion;
        this.motivo = motivo;
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(folio).append("\n");
        sb.append(fecha).append("\n");
        sb.append(sancion).append("\n");
        sb.append(motivo).append("\n");
        sb.append(status).append("\n");
        return sb.toString();
    }

    @Override
    public int compareTo(Multa o) {
        if(fecha.compareTo(o.fecha)==0){
            return folio.compareTo(o.folio);
        }else{
            return fecha.compareTo(o.fecha);
        }
    }
    
    
}
