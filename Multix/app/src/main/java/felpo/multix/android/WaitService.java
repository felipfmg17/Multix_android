package felpo.multix.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import felpo.tools.Tool;

public class WaitService extends Service {
    private static final long DEFAULT_LONG_WAIT_TIME = Tool.DAY;
    private static final long DEFAULT_SHORT_WAIT_TIME = Tool.MINUTE;
    private static final long DEFAULT_START_WAIT_TIME = Tool.MINUTE;

    private Thread waitingThread;

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
    }

    private void sendNotification(){

    }



    private class RunSearch implements Runnable{

        @Override
        public void run() {
            try {
                Thread.sleep(DEFAULT_START_WAIT_TIME);

            } catch (InterruptedException e) { e.printStackTrace();}
        }
    }
}
