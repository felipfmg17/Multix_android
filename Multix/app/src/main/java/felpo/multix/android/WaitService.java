package felpo.multix.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;

import java.util.List;

import felpo.multix.R;
import felpo.multix.core.History;
import felpo.multix.core.Multa;
import felpo.multix.core.Multix;
import felpo.tools.Tool;

public class WaitService extends Service {
    private static final long DEFAULT_LONG_WAIT_TIME = Tool.DAY;
    private static final long DEFAULT_SHORT_WAIT_TIME = Tool.MINUTE;
    private static final long DEFAULT_START_WAIT_TIME = Tool.MINUTE;
    private static final int NOTIFICATION_ID = 17;

    private Thread waitingThread;
    private Common common;
    private Handler handler;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void defaultConfiguration(){
        waitingThread = new Thread(new RunSearch());
        common = new Common(this);
        handler = new Handler();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    private Notification createNotification(Multa m){
        Notification.Builder nb = new Notification.Builder(this);
        nb.setSmallIcon(R.drawable.ic_launcher);
        nb.setContentText(m.fecha + ", " + m.sancion);
        nb.setContentTitle(getString(R.string.new_notification_title));
        long[] pattern = {800,800,800,500,500,500,500,500,500,500,500};
        nb.setVibrate(pattern);
        nb.setLights(Color.CYAN, 500, 500);
        nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);
        nb.setContentIntent(pendingIntent);

        return nb.build();
    }



    private void sendNotification(History h){
        try{
            List<Multa> multas;
            if(common.oldHistoryExists()){
                History old = common.loadOldHistory();
                multas = Multix.extractDifferences(old,h);
            }else{
                multas = h.multas;
            }

            for(Multa m:multas){
                if( !m.status.equals(Multix.PAGADA)){
                    Notification n = createNotification(m);
                    notificationManager.notify(NOTIFICATION_ID, n);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    private void runNotification(final History h){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                sendNotification(h);
            }
        };

        handler.post(r);
    }



    private class RunSearch implements Runnable{

        @Override
        public void run() {
            try {
                Thread.sleep(DEFAULT_START_WAIT_TIME);
                String placa;
                long wait_time;

                while(true){
                    wait_time = DEFAULT_LONG_WAIT_TIME;
                    try{
                       if(common.defaultPlacaExists()){
                           placa = common.getDefaultPlaca();
                           History h = Multix.requestHistory(placa);
                           runNotification(h);
                           common.saveOldHistory(h);
                       }
                    }catch(Exception e){
                        if(!common.isNetworkAvailable())
                            wait_time = DEFAULT_SHORT_WAIT_TIME;
                    }
                    Thread.sleep(wait_time);
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }


}
