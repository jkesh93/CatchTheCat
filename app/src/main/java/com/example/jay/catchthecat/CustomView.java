package com.example.jay.catchthecat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Jay on 11/6/2016.
 */

public class CustomView extends SurfaceView implements SurfaceHolder.Callback{

    protected Context context;
    private Bitmap cat;
    private Bitmap ringo;
    DrawingThread thread;
    Paint infoPaint;
    Paint scorePaint;
    int x, y, rotat, speed, level, catRotation;
    int score;

    public CustomView(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
        context = ctx;
        cat = BitmapFactory.decodeResource(context.getResources(), R.drawable.cat);
        ringo = cat.copy(Bitmap.Config.ARGB_8888, true);
        ringo = resizeBitmap(ringo, 250, 250);
        /*
        Setup BitMaps
         */

        scorePaint = new Paint();
        scorePaint.setTextAlign(Paint.Align.LEFT);
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(48);

        infoPaint = new Paint();
        infoPaint.setTextAlign(Paint.Align.LEFT);
        infoPaint.setColor(Color.WHITE);
        infoPaint.setTextSize(48);

        x = 550;
        y = 555;
        rotat = 0;
        speed = 5;

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

    }

    public int getLevel(){
        return level;
    }

    public Bitmap resizeBitmap(Bitmap b, int newWidth, int newHeight) {

        int w = b.getWidth();
        int h = b.getHeight();
        float scaleWidth = ((float) newWidth) / w;
        float scaleHeight = ((float) newHeight) / h;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                b, 0, 0, w, h, matrix, false);
        b.recycle();
        return resizedBitmap;
    }

    public Bitmap RotateBitmap(Bitmap source, float degrees){
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        catRotation += degrees;
        catRotation = catRotation % 360;
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new DrawingThread(holder, context, this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v("touch event",event.getX() + "," + event.getY());

        double distance1 = Math.sqrt((x-event.getX()) * (x-event.getX()) + (y-event.getY()) * (y-event.getY()));
        if(distance1 < 300) {
            score++;
            if(score - 10 > 10 && score % 10 == 0){
                level++;
                speed+=7;
            }
            float[] dirVectors = {0, 90, 180, 270};
            int randomDirection = (int) (Math.random() * 4);
            float randomValue = dirVectors[randomDirection];
            ringo = RotateBitmap(ringo, randomValue);

        }

        return true; // true
    }

    public void customDraw(Canvas canvas){
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(ringo, x,y,null);
        canvas.drawText("Level " + level, 300, 50, infoPaint);
        canvas.drawText("Score " + score, 300, 100, scorePaint);

        if(catRotation == 0 && y > -20){
            y -= speed;
        }

        if(catRotation == 90 && x < getWidth() - 320){
            x += speed;
        }

        if(catRotation == 180 && y < getHeight() - 300){
            y += speed;
        }

        if(catRotation == 270 && x > 0){
            x -= speed;
        }


    }

    class DrawingThread extends Thread {
        private boolean running;
        private Canvas canvas;
        private SurfaceHolder holder;
        private Context context;
        private CustomView view;

        private int FRAME_RATE = 30;
        private double delay = 1.0 / FRAME_RATE * 1000;
        private long time;

        public DrawingThread(SurfaceHolder holder, Context c, CustomView v) {
            this.holder=holder;
            context = c;
            view = v;
            time = System.currentTimeMillis();
        }

        void setRunning(boolean r) {
            running = r;
        }

        @Override
        public void run() {
            super.run();
            while(running){
                if(System.currentTimeMillis() - time > delay) {
                    time = System.currentTimeMillis();
                    canvas = holder.lockCanvas();
                    if(canvas!=null){
                        view.customDraw(canvas);
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }



    }
}
