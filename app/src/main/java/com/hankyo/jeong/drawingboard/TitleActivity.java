package com.hankyo.jeong.drawingboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hankyo.jeong.drawingboard.databinding.PaintingMainBinding;
import com.hankyo.jeong.drawingboard.databinding.TitleMainBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TitleActivity extends Activity {
    TitleMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = TitleMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), PaintActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, 2000);
    }
}
