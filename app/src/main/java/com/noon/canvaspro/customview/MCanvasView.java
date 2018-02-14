package com.noon.canvaspro.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.noon.canvaspro.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ril on 14/2/18.
 */

public class MCanvasView extends View implements View.OnTouchListener {

    private Canvas  mCanvas;
    private Path    mPath;
    private Paint       mPaint;
    private List<Path> paths = new ArrayList<>();
    private List<Path> undonePaths = new ArrayList<>();
    private int defaultColor = Color.BLACK;
    private Bitmap bitmap;
    private Matrix mMatrix;
    private RectF mSrcRectF;
    private RectF mDestRectF;
    private String mText;

//    List<Pair<Path, Integer>> path_color_list = new ArrayList<>();



    public MCanvasView(Context context)   {
        this(context, null);

    }

    public MCanvasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MCanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(defaultColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);



        mMatrix = new Matrix();
        mSrcRectF = new RectF();
        mDestRectF = new RectF();

        mCanvas = new Canvas();
        mPath = new Path();
        paths.add(mPath);
    }

    public void addBitmap(Bitmap bitmap){


        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public void drawText(String text){
        mText = text;

    }

    public void setColor(int color){
        invalidate();
        mPaint.setColor(color);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (Path p : paths){
            canvas.drawPath(p, mPaint);
        }
        canvas.drawPath(mPath, mPaint);

        if(bitmap!=null){

            // Setting size of Source Rect
            mSrcRectF.set(0, 0,(float) (bitmap.getWidth()*2.5),(float) (bitmap.getHeight()*2.5));

            // Setting size of Destination Rect
            mDestRectF.set(0, 0, getWidth(), getHeight());

            // Scaling the bitmap to fit the PaintView
            mMatrix.setRectToRect( mSrcRectF , mDestRectF, Matrix.ScaleToFit.START);

            // Drawing the bitmap in the canvas
            canvas.drawBitmap(bitmap, mMatrix, mPaint);
        }
        if(null != mText){
            canvas.drawText(mText, 0, 0, mPaint);
        }

        // Redraw the canvas
        invalidate();
    }


    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        paths.add(mPath);
        mPath = new Path();
    }

    public void doUndo () {
        if (paths.size()>0)  {
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        } else  {
            Log.i("undo", "Undo elsecondition");
        }
    }

    public void doRedo (){
        if (undonePaths.size()>0)  {
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
        }
        else  {
            Log.i("undo", "Redo elsecondition");
        }
    }

    public boolean onTouch(View arg0, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }


}

