package felpo.multix.android;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import felpo.multix.R;
import felpo.multix.core.History;
import felpo.multix.core.Multa;
import felpo.multix.core.Multix;
import felpo.tools.Tool;

public class Common {
    private Context context;
    private final String DEFAULT_PLACA;
    private final String DEFAULT_PLACA_ERROR = "error_placa";
    private SharedPreferences sharedPreferences;
    private final String HISTORY_FILE_EXTENSION = ".ser";
    private final String TEMPORAL_HISTORY = "temporal_history";
    private final String DEFAULT_FLAG = "default_flag";
    public static final String HISTORY_SAVED = "history";


    public Common(Context context){
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        DEFAULT_PLACA = context.getString(R.string.default_placa_key);
    }

    public boolean defaultPlacaExists(){
        if(sharedPreferences.contains(DEFAULT_PLACA)){
            String placa = getDefaultPlaca();
            return Multix.validatePlaca(placa);
        }
        return false;
    }

    public String getDefaultPlaca(){
        return sharedPreferences.getString(DEFAULT_PLACA,DEFAULT_PLACA_ERROR).toUpperCase();
    }

    public boolean oldHistoryExists(){
        if(defaultPlacaExists()){
            String placa = getDefaultPlaca();
            File path = ATool.getAppFileDirectory(context);
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
        editor.putString(DEFAULT_PLACA,placa.toUpperCase());
        editor.commit();
    }

    public Notification createNotification(Multa m){
        Notification.Builder nb = new Notification.Builder(context);
        nb.setAutoCancel(true);
        nb.setSmallIcon(R.drawable.multix3);
        nb.setContentText(m.fecha + ", " + m.motivo);
        nb.setContentTitle(context.getString(R.string.new_notification_title));
        long[] pattern = {500,500,500,500};
        nb.setVibrate(pattern);
        nb.setLights(Color.CYAN, 500, 500);
        nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent, 0);
        nb.setContentIntent(pendingIntent);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car11);
        nb.setLargeIcon(bitmap);
        return nb.build();
    }

    public void erasePreviousHistories(){
        if(!defaultPlacaExists())
            return;
        String placa = getDefaultPlaca() + HISTORY_FILE_EXTENSION;
        for(File f: context.getFilesDir().listFiles())
            if( !placa.equals( f.getName() ) )
                f.delete();
    }

    public boolean temporalHistoryExists(){
        File path = ATool.getAppFileDirectory(context);
        File f = new File(path,TEMPORAL_HISTORY+HISTORY_FILE_EXTENSION);
        return f.exists();
    }

    public void saveTemporalHistory(History history) throws IOException {
        String name = TEMPORAL_HISTORY + HISTORY_FILE_EXTENSION;
        OutputStream out = context.openFileOutput(name, context.MODE_PRIVATE);
        Tool.write(history, out);
        out.close();

    }

    public History loadTemporalHistory() throws IOException, ClassNotFoundException {
        String name = TEMPORAL_HISTORY + HISTORY_FILE_EXTENSION;
        InputStream in = context.openFileInput(name);
        History history = (History) Tool.readObject(in);
        in.close();
        return history;
    }

    public void eraseTemporaryHistory(){
        if(temporalHistoryExists()){
            File path = ATool.getAppFileDirectory(context);
            File f = new File(path,TEMPORAL_HISTORY+HISTORY_FILE_EXTENSION);
            f.delete();
        }
    }

    public void turnOnDefault(){
        SharedPreferences sharedPreferences = ATool.getAppSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DEFAULT_FLAG,true);
        editor.commit();
    }

    public void turnOffDefault(){
        SharedPreferences sharedPreferences = ATool.getAppSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DEFAULT_FLAG, false);
        editor.commit();
    }

    public boolean  isDefaultOn(){
        SharedPreferences sharedPreferences = ATool.getAppSharedPreferences(context);
        return sharedPreferences.getBoolean(DEFAULT_FLAG,false);
    }


}
