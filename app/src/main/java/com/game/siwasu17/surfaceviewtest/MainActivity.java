package com.game.siwasu17.surfaceviewtest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            final EditText editView = new EditText(MainActivity.this);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(editView);
            // OKボタンの設定
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String t = editView.getText().toString();
                    // OKボタンをタップした時の処理をここに記述
                    System.out.println("Enter: " + t);
                    MainActivity.this.text = t;
                }
            });

            // キャンセルボタンの設定
            builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // キャンセルボタンをタップした時の処理をここに記述
                }
            });


            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    InputMethodManager inputMethodManager
                            = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(editView, 0);
                }
            });
            dialog.show();


        }

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
    private String text = "";
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
