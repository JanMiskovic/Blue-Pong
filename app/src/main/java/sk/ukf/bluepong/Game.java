package sk.ukf.bluepong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class Game extends View {

    private Paint paint;
    private Paddle hostPaddle;
    private Paddle clientPaddle;
    private static int width;
    private static int height;

    public Game(Context context) {
        super(context);
        initialize(context);
    }

    public Game(Context context, AttributeSet attrs){
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screen = new Point();
        display.getSize(screen);
        width = screen.x;
        height = screen.y;

        paint = new Paint();
        hostPaddle = new Paddle(width / 2, (int) (height * 0.95));
        clientPaddle = new Paddle(width / 2, height / 20);

        System.out.println(GameActivity.getIsHost());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(0xFF2F82FF);

        canvas.drawRect(hostPaddle.getX() - width / 6,
                hostPaddle.getY() - height / 50,
                hostPaddle.getX() + width / 6,
                hostPaddle.getY() + height / 50, paint);

        canvas.drawRect(clientPaddle.getX() - width / 6,
                clientPaddle.getY() - height / 50,
                clientPaddle.getX() + width / 6,
                clientPaddle.getY() + height / 50, paint);
    }

    public void update() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (GameActivity.getIsHost())
            hostPaddle.setX((int) event.getX());
        else
            clientPaddle.setX((int) event.getX());

        System.out.println(GameActivity.getIsHost());
        System.out.println(this.getRotation());
        invalidate();
        return true;
    }

    public static int getW() { return width; }
    public static int getH() { return height; }
}
