package felpo.multix.android;


import android.app.Notification;
import android.app.NotificationManager;
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
        Log.i(DEBUG_TAG,m);
    }

    public static void toastD(Context c, String m){
        Toast.makeText(c,m,Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void buildNotification(Context context, String title, String message, int image){
        Notification.Builder nb = new Notification.Builder(context);
        nb.setContentTitle(title);
        nb.setContentText(message);
        nb.setSmallIcon(image);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,nb.build());
    }
}
