package com.hankyo.jeong.drawingboard.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hankyo.jeong.drawingboard.R;
import com.hankyo.jeong.drawingboard.databinding.ImageResizeViewBinding;

public class ImageResizeView extends RelativeLayout {

    private static final String TAG = "ImageResizeView";

    RelativeLayout rootLayout;
    public ImageView targetImage;
    public ImageButton cancelBtn;
    public ImageButton okBtn;
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
//        v.setScaleX(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics()));
//        v.setScaleY(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics()));
        addView(v);

        rootLayout = (RelativeLayout) findViewById(R.id.imageResizeViewLayout);
        targetImage = (ImageView) findViewById(R.id.targetImage);
        cancelBtn = (ImageButton) findViewById(R.id.cancelBtn);
//        okBtn = (ImageButton) findViewById(R.id.okBtn);
    }

    public void setTargetImage(Bitmap targetBitmap) {
        BitmapDrawable targetBitDrawable = new BitmapDrawable(getResources(), targetBitmap);
        targetImage.setBackground(targetBitDrawable);
    }

    public void setTargetImage(Bitmap targetBitmap, int parentWidth, int parentHeight) {
//        Log.d(TAG, "Parent W, H" + parentWidth + ", " + parentHeight);

        float changedTargetWidth = 0, changedTargetHeight = 0;

        int targetBitmapWidth = targetBitmap.getWidth();
        int targetBitmapHeight = targetBitmap.getHeight();

        // Determining whether it's landscape or portrait
        if (targetBitmapWidth > targetBitmapHeight) {         // LandScape
            changedTargetWidth = parentWidth / 2;
            float ratio = changedTargetWidth / targetBitmapWidth;
            changedTargetHeight = targetBitmapHeight * ratio;
        } else {         // Portrait
            changedTargetHeight = parentHeight / 2;
            float ratio = changedTargetHeight / targetBitmapHeight;
            changedTargetWidth = targetBitmapWidth * ratio;
        }

        if (changedTargetWidth != 0 && changedTargetHeight != 0) {
            targetImage.getLayoutParams().width = (int)changedTargetWidth;
            targetImage.getLayoutParams().height = (int)changedTargetHeight;
        }

        setTargetImage(targetBitmap);
    }
}
