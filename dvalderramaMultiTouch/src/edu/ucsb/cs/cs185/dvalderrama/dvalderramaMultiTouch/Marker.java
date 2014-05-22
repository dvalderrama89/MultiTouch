package edu.ucsb.cs.cs185.dvalderrama.dvalderramaMultiTouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Marker extends SurfaceView{
	private final SurfaceHolder surfaceHolder;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Marker(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
    }
    public Marker(Context context, AttributeSet attrs){
		super(context, attrs);
		surfaceHolder = getHolder();
		surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
	}
	public Marker(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		surfaceHolder = getHolder();
		surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
	}

    public boolean onTouch(MotionEvent event, Matrix matrix) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawCircle(event.getX(), event.getY(), 25, paint);
                canvas.setMatrix(matrix);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        return false;
    }
}
