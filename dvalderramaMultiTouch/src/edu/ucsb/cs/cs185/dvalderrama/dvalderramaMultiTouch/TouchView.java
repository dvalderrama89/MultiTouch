package edu.ucsb.cs.cs185.dvalderrama.dvalderramaMultiTouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class TouchView extends ImageView{
	//vars
	float[] lastEvent = null;
    float d = 0f;
    float newRot = 0f;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    public static String fileNAME;
    public static int framePos = 0;

    private float scale = 0;
    private float newDist = 0;

    // Fields
    private String TAG = this.getClass().getSimpleName();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // Remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    float oldDist = 1f;
	
	
	//public constructors for ImageView
	public TouchView(Context context) {
		super(context);
	}
	public TouchView(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	public TouchView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	public boolean onTouch(ImageView v, MotionEvent event, Bitmap bitmap) {
		ImageView view = v;
		
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			Log.d("touchview", "down");
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			lastEvent = null;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			lastEvent = new float[4];
			lastEvent[0] = event.getX(0);
			lastEvent[1] = event.getX(1);
			lastEvent[2] = event.getY(0);
			lastEvent[3] = event.getY(1);
			d = rotation(event);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			lastEvent = null;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("touchview", "move");
			if (mode == DRAG) {
				Log.d("touchview", "drag");
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM && event.getPointerCount() == 2) {
				Log.d("touchview", "zoom");
				float newDist = spacing(event);
				matrix.set(savedMatrix);
				if (newDist > 10f) {
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
				if (lastEvent != null) {
					newRot = rotation(event);
					float r = newRot - d;
					matrix.postRotate(r, view.getMeasuredWidth() / 2,
							view.getMeasuredHeight() / 2);
				}
			}
			break;
		}

		view.setImageMatrix(matrix);
		//view.setBackgroundDrawable(drawable);
		//view.setImageBitmap(bitmap);

		return true;
	}
	public Matrix getMatrix()
	{
		return matrix;
	}
	//rotation
	private float rotation(MotionEvent event) {
		Log.d("touchview", "rotate");
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);

		return (float) Math.toDegrees(radians);
	}
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}
