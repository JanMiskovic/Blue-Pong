package sk.ukf.bluepong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;

public class Game extends View {

    private Paint paint;

    public Game(Context context) {
        super(context);
        initialize();
    }

    public Game(Context context, AttributeSet attrs){
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        paint = new Paint();
        if (!GameActivity.getIsHost()) {
            this.setRotation(180);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(0xFF2F82FF);
        canvas.drawRect(20, 20, 300, 300, paint);
    }

    public void update() {

    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        return super.onDragEvent(event);
    }
}
