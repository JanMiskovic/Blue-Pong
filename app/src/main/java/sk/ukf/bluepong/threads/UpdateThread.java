package sk.ukf.bluepong.threads;

import android.os.Handler;

public class UpdateThread extends Thread {

    private Handler handler;
    private long now;
    private long updateTime;
    private long wait;
    private long timeFps = 1000000000 / 40;

    public UpdateThread(Handler handler) {
        super();
        this.handler = handler;
    }

    public void run() {
        while (true) {
            now = System.nanoTime();
            updateTime = System.nanoTime() - now;
            wait = (timeFps - updateTime) / 1000000;
            try {
                sleep(wait);
            } catch (Exception ignored) {}

            handler.sendEmptyMessage(0);

            if (Thread.currentThread().isInterrupted())
                return;
        }
    }
}
