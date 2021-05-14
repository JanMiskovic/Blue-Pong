package sk.ukf.bluepong;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.nio.ByteBuffer;
import java.util.UUID;

import sk.ukf.bluepong.activities.GameActivity;
import sk.ukf.bluepong.threads.ConnectedThread;
import sk.ukf.bluepong.threads.ConnectingThread;
import sk.ukf.bluepong.threads.ListeningThread;
import sk.ukf.bluepong.threads.UpdateThread;

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
    private boolean isHost;

    private Handler updateHandler;
    private UpdateThread updateThread;

    private Paint paint;
    private Paddle myPaddle;
    private Paddle opponentPaddle;
    private Ball ball;
    private static int width;
    private static int height;
    private GameActivity ga;
    private boolean gameIsRunning;

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
        ga = ((GameActivity) context);

        createUpdateHandler();
        updateThread = new UpdateThread(updateHandler);

        if (connectedThread != null) connectedThread.cancel();
        connectedThread = new ConnectedThread(handler);

        isHost = ga.getIntent().getBooleanExtra("isHost", true);
        opponentMAC = ga.getIntent().getStringExtra("opponentMAC");

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
        canvas.drawRect(myPaddle.getX() - myPaddle.getWidth(),
                myPaddle.getY() - myPaddle.getHeight(),
                myPaddle.getX() + myPaddle.getWidth(),
                myPaddle.getY() + myPaddle.getHeight(), paint);

        canvas.drawRect(opponentPaddle.getX() - opponentPaddle.getWidth(),
                opponentPaddle.getY() - opponentPaddle.getHeight(),
                opponentPaddle.getX() + opponentPaddle.getWidth(),
                opponentPaddle.getY() + opponentPaddle.getHeight(), paint);

        paint.setColor(0xFFFFA500);
        if (ball != null) {
            canvas.drawOval(ball.getX() - ball.getWidth(),
                    ball.getY() - ball.getHeight(),
                    ball.getX() + ball.getWidth(),
                    ball.getY() + ball.getHeight(), paint);
        }
    }

    public void update() {
        ball.move();
        ball.checkPaddleCollision(myPaddle);
        ball.checkPaddleCollision(opponentPaddle);
        ball.checkSideWallCollision();
        ball.checkBallScored();
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED:
                    ga.opponentConnected();
                    ball = new Ball(width / 2, height / 2, height / 50, height / 50);
                    gameIsRunning = true;
                    updateThread.start();
                    break;

                case ACCEPTED:
                    ga.connectionSuccessful();
                    ball = new Ball(width / 2, height / 2, height / 50, height / 50);
                    gameIsRunning = true;
                    updateThread.start();
                    break;

                case MESSAGE_WRITE:
                    byte[] writeBuffer = (byte[]) msg.obj;
                    int x = (int) mapValue(ByteBuffer.wrap(writeBuffer).getInt(), 0, 100, 0, width);
                    myPaddle.setX(x);
                    invalidate();
                    break;

                case MESSAGE_READ:
                    byte[] readBuffer = (byte[]) msg.obj;
                    x = (int) mapValue(ByteBuffer.wrap(readBuffer).getInt(), 0, 100, 0, width);
                    opponentPaddle.setX(x);
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
        if (gameIsRunning) {
            sendMessageInt((int) mapValue((int) event.getX(), 0, width, 0, 100));
        }

        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        myPaddle = new Paddle(width / 2, height - height / 20, width / 6, height / 50 );
        opponentPaddle = new Paddle(width / 2, height / 20, width / 6, height / 50);

        // This is here because this method basically works like 'onLayoutLoaded' (LIDL solution for now)
        if (isHost) ga.waitingForConnection();
        else ga.tryingToConnect();
    }

    @SuppressLint("HandlerLeak")
    private void createUpdateHandler() {
        updateHandler = new Handler() {
            public void handleMessage(Message msg) {
                update();
                invalidate();
                super.handleMessage(msg);
            }
        };
    }

    public void killThreads() {
        if (listenThread != null) listenThread.cancel();
        if (connectingThread != null) connectingThread.cancel();
        if (connectedThread != null) connectedThread.cancel();
    }

    private float mapValue(float value, float istart, float istop, float ostart, float ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    public static int getW() { return width; }
    public static int getH() { return height; }
}
