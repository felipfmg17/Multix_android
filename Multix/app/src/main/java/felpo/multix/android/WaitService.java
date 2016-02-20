package felpo.multix.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import felpo.multix.core.History;
import felpo.multix.core.Multa;
import felpo.multix.core.Multix;
import felpo.tools.Tool;

public class WaitService extends Service {
    private static final long COMMON_DELAY = Tool.HOUR*8;
    private static final long NETWORK_DELAY = 5*Tool.MINUTE;
    private static final long START_DELAY = 10*Tool.MINUTE;
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
        }, START_DELAY, COMMON_DELAY, TimeUnit.MILLISECONDS);

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
                common.turnOnDefault();
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
                    Notification n = common.createNotification(m);
                    handler.post(new RunShowNotification(n));
                }
            }
        }
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
