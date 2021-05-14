package sk.ukf.bluepong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.UUID;

import sk.ukf.bluepong.activities.GameActivity;
import sk.ukf.bluepong.threads.ConnectedThread;
import sk.ukf.bluepong.threads.ConnectingThread;
import sk.ukf.bluepong.threads.ListeningThread;

public class Game extends View {

    public static final int CONNECTED = 0;
    public static final int ACCEPTED = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;

    private BluetoothAdapter ba;
    private BluetoothDevice opponent;
    private ListeningThread listenThread = null;
    private ConnectingThread connectingThread = null;
    private ConnectedThread connectedThread = null;
    private String opponentMAC;
    private boolean opponentConnected;

    private Paint paint;
    private Paddle myPaddle;
    private Paddle opponentPaddle;
    private static int width;
    private static int height;
    private boolean isHost;

    public Game(Context context) {
        super(context);
        initialize(context);
    }

    public Game(Context context, AttributeSet attrs){
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        ba = BluetoothAdapter.getDefaultAdapter();

        paint = new Paint();

        if (connectedThread != null) connectedThread.cancel();
        connectedThread = new ConnectedThread(handler);

        isHost = ((Activity) context).getIntent().getBooleanExtra("isHost", true);
        opponentMAC = ((Activity) context).getIntent().getStringExtra("opponentMAC");

        if (isHost) waitForConnection();
        else tryToConnect();
    }

    private void waitForConnection() {
        if (listenThread != null) listenThread.cancel();
        listenThread = new ListeningThread(BluetoothAdapter.getDefaultAdapter(),
                UUID.fromString("00112233-afac-1234-abcd-abcdef012345"),
                connectedThread, handler);
        listenThread.start();
    }

    private void tryToConnect() {
        opponent = ba.getRemoteDevice(opponentMAC);
        if (connectingThread != null) connectingThread.cancel();
        connectingThread = new ConnectingThread(BluetoothAdapter.getDefaultAdapter(),
                opponent, UUID.fromString("00112233-afac-1234-abcd-abcdef012345"),
                connectedThread, handler);
        connectingThread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(0xFF2F82FF);

        canvas.drawRect(myPaddle.getX() - width / 6,
                myPaddle.getY() - height / 50,
                myPaddle.getX() + width / 6,
                myPaddle.getY() + height / 50, paint);

        canvas.drawRect(opponentPaddle.getX() - width / 6,
                opponentPaddle.getY() - height / 50,
                opponentPaddle.getX() + width / 6,
                opponentPaddle.getY() + height / 50, paint);

    }

    public void update() {

    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED:
                    opponentConnected = true;
                    break;

                case ACCEPTED:
                    opponentConnected = true;
                    break;

                case MESSAGE_WRITE:
                    byte[] writeBuffer = (byte[]) msg.obj;
                    myPaddle.setX(ByteBuffer.wrap(writeBuffer).getInt());
                    invalidate();
                    break;

                case MESSAGE_READ:
                    byte[] readBuffer = (byte[]) msg.obj;
                    opponentPaddle.setX(ByteBuffer.wrap(readBuffer).getInt());
                    invalidate();
                    break;
            }
        }
    };

    public void sendMessageInt(int message) {
        byte[] writeBuffer = ByteBuffer.allocate(4).putInt(message).array();
        connectedThread.write(writeBuffer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (opponentConnected) {
            sendMessageInt((int) event.getX());
        }

        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        myPaddle = new Paddle(width / 2, height - height / 20);
        opponentPaddle = new Paddle(width / 2, height / 20);
    }

    public static int getW() { return width; }
    public static int getH() { return height; }
}
