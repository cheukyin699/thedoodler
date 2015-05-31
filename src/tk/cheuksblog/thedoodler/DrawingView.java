package tk.cheuksblog.thedoodler;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
	
	public int currentColor = color.black;
	public int strokeWidth = 20;
	
	private Path drawingPath;
	private Paint drawPaint;
	private Canvas bgCanvas, fgCanvas, mergedCanvas;
	private Bitmap bgBitmap, fgBitmap, mergedBitmap;
	
	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawing();
	}
	
	public Bitmap getBitmap() {
		mergeLayers();
		return mergedBitmap;
	}
	
	public void setBitmap(Bitmap bm) {
		bgBitmap = Bitmap.createScaledBitmap(bm, bgBitmap.getWidth(), bgBitmap.getHeight(), false).copy(Bitmap.Config.ARGB_8888, true);
		postInvalidate();
	}
	
	public void reset_fg() {
		fgBitmap = Bitmap.createBitmap(fgBitmap.getWidth(), fgBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		fgCanvas = new Canvas(fgBitmap);
		mergeLayers();
		postInvalidate();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		bgBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		fgBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mergedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bgCanvas = new Canvas(bgBitmap);
		fgCanvas = new Canvas(fgBitmap);
		bgCanvas.drawColor(Color.WHITE);
		mergedCanvas = new Canvas(mergedBitmap);
		
		mergeLayers();
		postInvalidate();
	}
	
	public void mergeLayers() {
		mergedCanvas.drawBitmap(bgBitmap, 0, 0, drawPaint);
		mergedCanvas.drawBitmap(fgBitmap, 0, 0, drawPaint);
	}
	
	@Override
	protected void onDraw(Canvas cv) {
		mergeLayers();
		cv.drawBitmap(mergedBitmap, 0, 0, drawPaint);
		cv.drawPath(drawingPath, drawPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		float tx = evt.getX(), ty = evt.getY();
		
		switch(evt.getAction()) {
		case MotionEvent.ACTION_DOWN:
			drawingPath.moveTo(tx, ty);
			break;
		case MotionEvent.ACTION_MOVE:
			drawingPath.lineTo(tx, ty);
			break;
		case MotionEvent.ACTION_UP:
			fgCanvas.drawPath(drawingPath, drawPaint);
			drawingPath.reset();
			break;
		default:
			return false;
		}
		
		// Forces the view to redraw
		invalidate();
		
		return true;
	}
	
	private void setupDrawing() {
		drawingPath = new Path();
		drawPaint = new Paint(Paint.DITHER_FLAG);
		
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(strokeWidth);
		drawPaint.setColor(currentColor);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
	}
	
	public void updateColor(int color) {
		currentColor = color;
		drawPaint.setColor(currentColor);
	}
	
	public void updateBS(int size) {
		strokeWidth = size;
		drawPaint.setStrokeWidth(strokeWidth);
	}
}
