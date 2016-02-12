package felpo.multix.android;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import felpo.multix.core.History;
import felpo.tools.Tool;

public class Common {
    private Context context;
    private final String DEFAULT_PLACA = "placa";
    private final String DEFAULT_PLACA_ERROR = "error_placa";
    private SharedPreferences sharedPreferences;
    private final String HISTORY_FILE_EXTENSION = ".ser";

    public Common(Context context){
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean defaultPlacaExists(){
        return sharedPreferences.contains(DEFAULT_PLACA);
    }

    public String getDefaultPlaca(){
        return sharedPreferences.getString(DEFAULT_PLACA,DEFAULT_PLACA_ERROR);
    }

    public boolean oldHistoryExists(){
        if(defaultPlacaExists()){
            String placa = getDefaultPlaca();
            String path = ATool.getAppDirectory(context);
            File f = new File(path,placa+ HISTORY_FILE_EXTENSION);
            if(f.exists())
                return true;
        }
        return false;
    }

    public History loadOldHistory() throws IOException, ClassNotFoundException {
        FileInputStream in = context.openFileInput(getDefaultPlaca() + HISTORY_FILE_EXTENSION);
        History h = (History) Tool.readObject(in);
        in.close();
        return h;
    }

    public void saveOldHistory(History old) throws IOException {
        FileOutputStream out = context.openFileOutput(getDefaultPlaca() + HISTORY_FILE_EXTENSION,context.MODE_PRIVATE);
        Tool.write(old, out);
        out.close();
    }

    public boolean isNetworkAvailable() {
        return ATool.isNetworkAvailable(context);
    }

    public void setDefaultPlaca(String placa){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEFAULT_PLACA,placa);
        editor.commit();
    }
}
