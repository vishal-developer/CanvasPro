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
import android.support.annotation.FloatRange;
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

    public static final String TAG = MCanvasView.class.getSimpleName();

    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private List<Path> paths = new ArrayList<>();
    private List<Path> undonePaths = new ArrayList<>();
    private List<Paint> paints = new ArrayList<>();
    private int defaultColor = Color.BLACK;
    private float defaultStroke = 5.0F;
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private RectF mSrcRectF;
    private RectF mDestRectF;
    private String mText = "";

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private float textX, textY;


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

        textX = (float)(getWidth()/2.5);
        textY = (float)(getHeight()/2.5);

        createPaint();

        mMatrix = new Matrix();
        mSrcRectF = new RectF();
        mDestRectF = new RectF();

        mCanvas = new Canvas();
        mPath = new Path();
    }

    public void addBitmap(Bitmap bitmap){
        mBitmap = bitmap;
    }



    public Bitmap getBitmap(){
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bmp;
    }



    public void drawText(String text, float mX, float mY){
        mText = text;
        textX = mX;
        textY = mY;
        invalidate();
    }

    public void drawText(String text){
        mText = text;
        invalidate();

        /*Log.d(TAG, "mText :"+mText+" textX:"+textX+" textY:"+textY);
        if(mText != null && mText.length()>0){
            mCanvas.drawText(mText, 300, 300, mPaint);
        }*/
    }

    public void setColor(int color){
        invalidate();
        defaultColor = color;
    }

    public void setPaintWidth(float widthPx) {
            invalidate();
            defaultStroke = widthPx;
            mPaint.setStrokeWidth(defaultStroke);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(defaultColor);
        for(int i = 0; i<paths.size(); i++){
            canvas.drawPath(paths.get(i), paints.get(i));
        }
        if(mBitmap!=null){
            // Setting size of Source Rect
            mSrcRectF.set(0, 0,(float) (mBitmap.getWidth()*2.5),(float) (mBitmap.getHeight()*2.5));

            // Setting size of Destination Rect
            mDestRectF.set(0, 0, getWidth(), getHeight());

            // Scaling the bitmap to fit the PaintView
            mMatrix.setRectToRect( mSrcRectF , mDestRectF, Matrix.ScaleToFit.START);

            // Drawing the bitmap in the canvas
            canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        }
        Log.d(TAG, "mText :"+mText+" textX:"+textX+" textY:"+textY);
        if(mText != null && mText.length()>0){
            canvas.drawText(mText, textX, textY, mPaint);
        }
        canvas.drawPath(mPath, mPaint);
    }


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

        paints.add(mPaint);
        createPaint();

        mPath = new Path();
    }


    private void createPaint(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(defaultColor);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(defaultStroke);

        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(24);
    }

    public void doUndo () {
        if (paths.size()>0)  {
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        }
    }

    public void doRedo (){
        if (undonePaths.size()>0)  {
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
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

