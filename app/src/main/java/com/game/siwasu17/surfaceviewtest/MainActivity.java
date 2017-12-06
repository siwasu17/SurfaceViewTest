package com.game.siwasu17.surfaceviewtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private ImageView imageView = null;
    private SurfaceView surfaceView = null;
    private SurfaceHolder holder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.main_surface);
        //最前面化
        surfaceView.setZOrderOnTop(true);

        holder = surfaceView.getHolder();
        //ビューの背景を透過
        holder.setFormat(PixelFormat.TRANSLUCENT);
        holder.addCallback(this);

        imageView = (ImageView) findViewById(R.id.manga_view);
        Picasso.with(this)
                .load(R.drawable.iiwake)
                .into(imageView);

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startDrawThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopDrawThread();
    }

    /**
     * メインループ
     */
    private static final long FPS = 60;

    private class DrawThread extends Thread {
        boolean isFinished;

        @Override
        public void run() {
            while (!isFinished) {

                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    onDraw(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }
                try {
                    sleep(1000 / FPS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private DrawThread drawThread;

    public void startDrawThread() {
        stopDrawThread();

        drawThread = new DrawThread();
        drawThread.start();
    }

    public boolean stopDrawThread() {
        if (drawThread == null) {
            return false;
        }

        drawThread.isFinished = true;
        drawThread = null;
        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            x = (int) event.getX();
            y = (int) event.getY();
        }
        return true;
    }

    /**
     * 描画系
     */
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String text = ">";
    private int x = 0;
    private int y = 0;

    void onDraw(Canvas canvas) {

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        //透明で塗りつぶし
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.font_size));
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(text + ": " + x + ", " + y, x, y, paint);
    }

}
