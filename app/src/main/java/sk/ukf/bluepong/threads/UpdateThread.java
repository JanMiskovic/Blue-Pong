package sk.ukf.bluepong.threads;

import android.os.Handler;

public class UpdateThread extends Thread {

    private Handler handler;

    public UpdateThread(Handler handler) {
        super();
        this.handler = handler;
    }

    public void run() {
        while (true) {
            try {
                sleep(30);
            } catch (Exception ignored) {}

            handler.sendEmptyMessage(0);

            if (Thread.currentThread().isInterrupted())
                return;
        }
    }
}
