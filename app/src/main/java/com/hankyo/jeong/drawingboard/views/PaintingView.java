package com.hankyo.jeong.drawingboard.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class PaintingView extends View {

    enum DrawMode {DRAW_LINE, DRAW_RECTANGLE};

    private static final String TAG = "PaintingView";

    ArrayList<Path> pathList = new ArrayList<Path>();

    private Paint paint = new Paint();
    private Path path  = new Path();

    private Canvas drawCanvas;
    private Paint canvasPaint;
    private Bitmap canvasBitmap;

    private int paintColor = 0xFF000000;

    private DrawMode drawMode = DrawMode.DRAW_LINE;

    private float recPivotX, recPivotY, recMoveX, recMoveY = 0;

    public PaintingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(10f);
        paint.setColor(paintColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "ondraw");

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        if (path != null)
            canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                recPivotX = touchX;
                recPivotY = touchY;

                // Draw Line Mode
                if (drawMode == DrawMode.DRAW_LINE) {
                    path.moveTo(recPivotX, recPivotY);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                recMoveX = touchX;
                recMoveY = touchY;

                // Draw Line Mode
                if (drawMode == DrawMode.DRAW_LINE) {
                    path.lineTo(recMoveX, recMoveY);
                }
                else if (drawMode == DrawMode.DRAW_RECTANGLE) {
                    drawRectangle();
                }
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(path, paint);
                path.reset();

                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    private void drawRectangle() {
        path.reset();
        if (recPivotX - recMoveX < 0)
        {
            if (recPivotY - recMoveY < 0)
                path.addRect(recPivotX, recPivotY, recMoveX, recMoveY, Path.Direction.CW);
            else
                path.addRect(recPivotX, recMoveY, recMoveX, recPivotY, Path.Direction.CW);
        }
        else
        {
            if (recPivotY - recMoveY < 0)
                path.addRect(recMoveX, recPivotY, recPivotX, recMoveY, Path.Direction.CW);
            else
                path.addRect(recMoveX, recMoveY, recPivotX, recPivotY, Path.Direction.CW);
        }
    }

    public void setColor(String newColor) {
        invalidate();

        paintColor = Color.parseColor(newColor);
        paint.setColor(paintColor);
    }

    public void setTool(String newTool) {
        invalidate();

        if (newTool.equals("line")) {
            drawMode = DrawMode.DRAW_LINE;
        } else if (newTool.equals("rectangle")) {
            drawMode = DrawMode.DRAW_RECTANGLE;
        }
    }

    public void undoDrawing() {
        Path pastPath = pathList.get(pathList.size() - 2);
        path = pastPath;
        drawCanvas.drawPath(path, paint);

        invalidate();
    }
}
