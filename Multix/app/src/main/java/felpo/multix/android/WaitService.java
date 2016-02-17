package felpo.multix.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import felpo.multix.R;
import felpo.multix.core.History;
import felpo.multix.core.Multa;
import felpo.multix.core.Multix;
import felpo.tools.Tool;

public class WaitService extends Service {
    private static final long DELAY = Tool.DAY;
    private static final long NETWORK_DELAY = 5*Tool.MINUTE;
    private static final long START_DELAY = Tool.MINUTE;
    private static final int NOTIFICATION_ID = 17;

    private ScheduledExecutorService scheduler;
    private Common common;
    private Handler handler;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }




    private void init(){
        common = new Common(this);
        handler = new Handler();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                searchMultas();
            }
        }, START_DELAY, DELAY, TimeUnit.MILLISECONDS);

    }

    private void searchMultas(){
        if(!common.defaultPlacaExists())
            return;

        try{
            while(true){

                if(!common.isNetworkAvailable()){
                    Thread.sleep(NETWORK_DELAY);
                    continue;
                }

                String placa = common.getDefaultPlaca();
                History history = Multix.requestHistory(placa);
                processHistory(history);
                common.saveOldHistory(history);
                break;
            }
        }catch ( Exception e){
            e.printStackTrace();
        }

    }

    private void processHistory(History history) throws Exception {
        if(common.oldHistoryExists()) {
            History old = common.loadOldHistory();
            List<Multa> multas = Multix.extractDifferences(old, history);
            for (Multa m : multas) {
                if (!m.status.equals(Multix.PAGADA)) {
                    Notification n = createNotification(m);
                    handler.post(new RunShowNotification(n));
                }
            }
        }
    }

    private Notification createNotification(Multa m){
        Notification.Builder nb = new Notification.Builder(this);
        nb.setAutoCancel(true);
        nb.setSmallIcon(R.drawable.multix3);
        nb.setContentText(m.fecha + ", " + m.sancion);
        nb.setContentTitle(getString(R.string.new_notification_title));
        long[] pattern = {200,200,200,200,200,200};
        nb.setVibrate(pattern);
        nb.setLights(Color.CYAN, 500, 500);
        nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);
        nb.setContentIntent(pendingIntent);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mini_multix);
        nb.setLargeIcon(bitmap);
        return nb.build();
    }

    private void showNotification(Notification n){
        notificationManager.notify(NOTIFICATION_ID, n);
    }

    private class RunShowNotification implements Runnable{
        Notification n;

        private RunShowNotification(Notification n) {
            this.n = n;
        }

        @Override
        public void run() {
            showNotification(n);
        }
    }


}
