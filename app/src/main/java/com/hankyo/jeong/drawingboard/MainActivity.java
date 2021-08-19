package com.hankyo.jeong.drawingboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.hankyo.jeong.drawingboard.databinding.ActivityMainBinding;
import com.hankyo.jeong.drawingboard.views.PaintingView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    private PaintingView paintingView;
    private ImageButton currPaint;
    private ImageButton currTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        paintingView = binding.paintView;
        LinearLayout colorPalletteLayout = binding.colorPallette;
        currPaint = (ImageButton) colorPalletteLayout.getChildAt(0);
    }

    public void changeColor(View view) {
        if (view != currPaint) {
            String color = view.getTag().toString();
            paintingView.setColor(color);
            currPaint = (ImageButton) view;
        }
    }

    public void changeTool(View view) {
        if (view != currTool) {
            String tool = view.getTag().toString();
            paintingView.setTool(tool);
            currTool = (ImageButton) view;
        }
    }

    public void getPhotoData(View view) {

    }

    public void undoDrawing(View view) {
        Log.d(TAG, "undo Drawing");
        paintingView.undoDrawing();
    }

    public void redoDrawing(View view) {
        Log.d(TAG, "redo Drawing");
        paintingView.redoDrawing();
    }
}