package felpo.multix.core;

import java.util.*;
import java.io.*;

public class History implements Serializable{
    public String placa;
    public List<Multa> multas;

    public History(String placa, List<Multa> multas) {
        this.placa = placa;
        this.multas = multas;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(placa).append("\n\n");
        for(Multa m:multas)
            sb.append(m.toString()).append("\n");
        return sb.toString();
    }
    
    
}
