package felpo.multix.android;


import android.content.Context;
import android.net.ConnectivityManager;

import felpo.multix.core.History;

public class Common {
    Context context;

    public Common(Context context){
        this.context = context;
    }

    public boolean defaultPlacaExists(){
        return false;
    }

    public String getDefaultPlaca(){
        return null;
    }

    public boolean oldHistoryExists(){
        return false;
    }

    public History loadOldHistory(){
        return null;
    }

    public void saveOldHistory(History old){

    }

    public boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void setDefaultPlaca(String placa){

    }
}
