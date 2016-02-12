package felpo.multix.android;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ATool {
    public static final String DEBUG_TAG = "felpo";

    public static String getAppDirectory(Context c){
        return c.getApplicationInfo().dataDir;
    }

    public static SharedPreferences getAppSharedPreferences(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c);
    }

    public static void d(String m){
        Log.d(DEBUG_TAG,m);
    }

    public static void toast(Context c, String m){
        Toast.makeText(c,m,Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
