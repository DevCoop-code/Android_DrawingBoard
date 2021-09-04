package com.hankyo.jeong.drawingboard.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class PaintingView extends View {

    enum DrawMode {DRAW_LINE, DRAW_RECTANGLE, DRAW_ERASE};

    private static final String TAG = "PaintingView";

    private ArrayList<Bitmap> canvasBitmapList = new ArrayList<Bitmap>();
    private int canvasBitmapCount = 0;

    private Paint paint = new Paint();
    private Path path  = new Path();

    private Canvas drawCanvas;
    private Paint canvasPaint;
    private Bitmap canvasBitmap;

    private int paintColor = 0xFF000000;

    private DrawMode drawMode = DrawMode.DRAW_LINE;

    private float recPivotX, recPivotY, recMoveX, recMoveY = 0;

    private PorterDuffXfermode clear;
    private PorterDuffXfermode draw;

    public PaintingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(10f);
        paint.setColor(paintColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        clear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        draw = new PorterDuffXfermode(PorterDuff.Mode.DST);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Bitmap intermediateMap = canvasBitmap.copy(canvasBitmap.getConfig(), true);
        canvasBitmapList.add(canvasBitmapCount, intermediateMap);
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
                if (drawMode == DrawMode.DRAW_LINE || drawMode == DrawMode.DRAW_ERASE) {
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
                else if (drawMode == DrawMode.DRAW_ERASE) {
                    path.lineTo(recMoveX, recMoveY);
                    drawCanvas.drawPath(path, paint);
                    path.reset();
                    path.moveTo(recPivotX, recPivotY);
                }
                else if (drawMode == DrawMode.DRAW_RECTANGLE) {
                    drawRectangle();
                }
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(path, paint);
                path.reset();

                canvasBitmapCount++;
                if (canvasBitmapCount > 0) {
                    Bitmap intermediateMap = canvasBitmap.copy(canvasBitmap.getConfig(), true);
                    if (canvasBitmapCount < canvasBitmapList.size()) {
                        Log.d(TAG, "bitmap SET count: " + canvasBitmapCount);
                        canvasBitmapList.set(canvasBitmapCount, intermediateMap);
                    }
                    else {
                        Log.d(TAG, "bitmap ADD count: " + canvasBitmapCount);
                        canvasBitmapList.add(intermediateMap);
                    }
                }
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    private void drawRectangle() {
        path.reset();
        if (recPivotX - recMoveX < 0) {
            if (recPivotY - recMoveY < 0)
                path.addRect(recPivotX, recPivotY, recMoveX, recMoveY, Path.Direction.CW);
            else
                path.addRect(recPivotX, recMoveY, recMoveX, recPivotY, Path.Direction.CW);
        }
        else {
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
            paint.setXfermode(null);
        } else if (newTool.equals("rectangle")) {
            drawMode = DrawMode.DRAW_RECTANGLE;
            paint.setXfermode(null);
        } else if (newTool.equals("eraser")) {
            drawMode = DrawMode.DRAW_ERASE;
            setEraser();
        }
    }

    public void undoDrawing() {
        canvasBitmapCount--;
        if (canvasBitmapCount >= 0) {
            Log.d(TAG, "bitmap GET count: " + canvasBitmapCount);
            canvasBitmap = canvasBitmapList.get(canvasBitmapCount).copy(canvasBitmap.getConfig(), true);
            drawCanvas = new Canvas(canvasBitmap);
            invalidate();
        }
        else {
            canvasBitmapCount = 0;
        }
    }

    public void redoDrawing() {
        canvasBitmapCount++;
        if (canvasBitmapCount < canvasBitmapList.size()) {
            Log.d(TAG, "bitmap GET count: " + canvasBitmapCount);
            canvasBitmap = canvasBitmapList.get(canvasBitmapCount).copy(canvasBitmap.getConfig(), true);
            drawCanvas = new Canvas(canvasBitmap);
            invalidate();
        }
        else {
            canvasBitmapCount = canvasBitmapList.size() - 1;
        }
    }

    public void drawPhotoImage(Bitmap bitmap, float left, float top, int w, int h) {
        Bitmap rescaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        drawCanvas.drawBitmap(rescaledBitmap, left, top, null);
        invalidate();

        canvasBitmapCount++;
        if (canvasBitmapCount > 0) {
            Bitmap intermediateMap = canvasBitmap.copy(canvasBitmap.getConfig(), true);
            if (canvasBitmapCount < canvasBitmapList.size()) {
                Log.d(TAG, "bitmap SET count: " + canvasBitmapCount);
                canvasBitmapList.set(canvasBitmapCount, intermediateMap);
            }
            else {
                Log.d(TAG, "bitmap ADD count: " + canvasBitmapCount);
                canvasBitmapList.add(intermediateMap);
            }
        }
    }

    public void setEraser() {
        if (clear != null) {
            paint.setXfermode(clear);
        }
    }
}
