package com.hankyo.jeong.drawingboard.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hankyo.jeong.drawingboard.R;
import com.hankyo.jeong.drawingboard.databinding.ImageResizeViewBinding;

public class ImageResizeView extends RelativeLayout {

    RelativeLayout rootLayout;
    ImageView targetImage;
    ImageButton cancelBtn;

    public ImageResizeView(Context context) {
        super(context);

        initView();
    }

    public ImageResizeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public ImageResizeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView();
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
        View v = li.inflate(R.layout.image_resize_view, this, false);
        addView(v);

        rootLayout = (RelativeLayout) findViewById(R.id.imageResizeViewLayout);
        targetImage = (ImageView) findViewById(R.id.targetImage);
        cancelBtn = (ImageButton) findViewById(R.id.cancelBtn);
    }

    public void setTargetImage(Bitmap targetBitmap) {
        BitmapDrawable targetBitDrawable = new BitmapDrawable(getResources(), targetBitmap);
        targetImage.setBackground(targetBitDrawable);
    }
}
