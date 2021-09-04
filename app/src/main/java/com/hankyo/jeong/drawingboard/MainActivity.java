package com.hankyo.jeong.drawingboard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hankyo.jeong.drawingboard.databinding.ActivityMainBinding;
import com.hankyo.jeong.drawingboard.utils.DialogCallback;
import com.hankyo.jeong.drawingboard.utils.Utils;
import com.hankyo.jeong.drawingboard.views.ImageResizeView;
import com.hankyo.jeong.drawingboard.views.PaintingToolElementAdapter;
import com.hankyo.jeong.drawingboard.views.PaintingToolListDecoration;
import com.hankyo.jeong.drawingboard.views.PaintingView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";

    private static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE"};

    private ActivityMainBinding binding;

    private PaintingView paintingView;
    private ImageButton currPaint;
    private ImageButton currTool;

    ActivityResultLauncher<Intent> requestActivity;

    // ImagePhoto Move Value
    float oldXvalue, oldYvalue;
    float targetX = 0, targetY = 0;
    float drawX = 0, drawY = 0;
    View targetImageView;

    private RecyclerView listview;
    private PaintingToolElementAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set RecyclerView
        listview = binding.mainListview;
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);
        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("0xFF000000");
        itemList.add("0xFFFFFFFF");
        itemList.add("0xFFED1C24");
//        itemList.add("3");
//        itemList.add("4");
//        itemList.add("5");
//        itemList.add("6");
//        itemList.add("7");
//        itemList.add("8");
//        itemList.add("9");
//        itemList.add("10");
//        itemList.add("11");
//        itemList.add("12");
//        itemList.add("13");
//        itemList.add("14");
        adapter = new PaintingToolElementAdapter(MainActivity.this, itemList, null);
        listview.setAdapter(adapter);

        PaintingToolListDecoration decoration = new PaintingToolListDecoration();
        listview.addItemDecoration(decoration);


        paintingView = binding.paintView;
        LinearLayout colorPalletteLayout = binding.colorPallette;
        currPaint = (ImageButton) colorPalletteLayout.getChildAt(0);

        requestActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    String phototImgPath = getImagePathFromURI(result.getData().getData());

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    Bitmap photoBitmap = BitmapFactory.decodeFile(phototImgPath, options);

//                    paintingView.drawPhotoImage(photoBitmap);
                    /*
                     Make ImageResizeView Dynamically
                     */
                    ImageResizeView imageResizeView = new ImageResizeView(getApplicationContext());
                    imageResizeView.setTargetImage(photoBitmap);
                    imageResizeView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
//                            int width = ((ViewGroup)view.getParent()).getWidth() - view.getWidth();
//                            int height = ((ViewGroup)view.getParent()).getHeight() - view.getHeight();
                            targetImageView = view.findViewById(R.id.targetImage);
                            int deltaX = (view.getWidth() - targetImageView.getWidth()) / 2;
                            int deltaY = (view.getHeight() - targetImageView.getHeight()) / 2;

                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    oldXvalue = motionEvent.getX();
                                    oldYvalue = motionEvent.getY();

                                    Log.d(TAG, "old Value X: " + oldXvalue + ", Y: " + oldYvalue);
                                    break;

                                case MotionEvent.ACTION_MOVE:
//                                    view.setX(motionEvent.getRawX() - oldXvalue);
//                                    view.setY(motionEvent.getRawY() - (oldYvalue + view.getHeight()));
                                    targetX = motionEvent.getRawX() - deltaX - (targetImageView.getWidth() / 2);
                                    targetY = motionEvent.getRawY() - deltaY - (targetImageView.getHeight() / 2);

                                    drawX = motionEvent.getRawX() - (targetImageView.getWidth() / 2);
                                    drawY = motionEvent.getRawY() - (targetImageView.getHeight() / 2);

                                    view.setX(targetX);
                                    view.setY(targetY);
                                    break;

                                case MotionEvent.ACTION_UP:
//                                    if (view.getX() > width && view.getY() > height) {
//                                        view.setX(width);
//                                        view.setY(height);
//                                    } else if (view.getX() < 0 && view.getY() > height) {
//                                        view.setX(0);
//                                        view.setY(height);
//                                    } else if (view.getX() > width && view.getY() < 0) {
//                                        view.setX(width);
//                                        view.setY(0);
//                                    } else if (view.getX() < 0 && view.getY() < 0) {
//                                        view.setX(0);
//                                        view.setY(0);
//                                    } else if (view.getX() < 0 || view.getX() > width) {
//                                        if (view.getX() < 0) {
//                                            view.setX(0);
//                                            view.setY(motionEvent.getRawY() - oldYvalue - view.getHeight());
//                                        } else {
//                                            view.setX(width);
//                                            view.setY(motionEvent.getRawY() - oldYvalue - view.getHeight());
//                                        }
//                                    } else if (view.getY() < 0 || view.getY() > height) {
//                                        if (view.getY() < 0) {
//                                            view.setX(motionEvent.getRawX() - oldXvalue);
//                                            view.setY(0);
//                                        } else {
//                                            view.setX(motionEvent.getRawX() - oldXvalue);
//                                            view.setY(height);
//                                        }
//                                    }
                                    break;
                            }

                            return true;
                        }
                    });
//                    imageResizeView.okBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            paintingView.drawPhotoImage(photoBitmap, targetX, targetY);
//                        }
//                    });

                    imageResizeView.cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "Draw PaintView: " + drawX + ", " + drawY);
                            paintingView.drawPhotoImage(photoBitmap, drawX, drawY, targetImageView.getWidth(), targetImageView.getHeight());

                            imageResizeView.setVisibility(View.INVISIBLE);
                        }
                    });
                    binding.paintingArea.addView(imageResizeView);
                }
            }
        });
    }

    private String getImagePathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null)
            return contentUri.getPath();
        else {
            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imgPath = cursor.getString(idx);
            cursor.close();

            return imgPath;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean externalStoragePermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!externalStoragePermissionAccepted)         // Not Accepted
                    {
                        DialogCallback callback = new DialogCallback() {
                            @Override
                            public void willDoAcceptCallback() {
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
                                    requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                            }

                            @Override
                            public void willDoDenyCallback() {
                                String warningPermsMsg = getResources().getString(R.string.permissionWarningMsg);
                                Toast.makeText(getApplicationContext(), warningPermsMsg, Toast.LENGTH_LONG).show();
                            }
                        };

                        String alertTitleMsg = getResources().getString(R.string.permissionTitleMsg);
                        String alertYesMsg = getResources().getString(R.string.permissionYesMsg);
                        String alertNoMsg = getResources().getString(R.string.permissionNoMsg);
                        String alertNeedMsg = getResources().getString(R.string.permissionNeedMsg);

                        Utils.showDialogForPermission(this, alertTitleMsg, alertYesMsg, alertNoMsg, alertNeedMsg, callback);

                    } else {                                        // Accepted
                        getPhotoData();
                    }
                }
                break;
        }
    }

    private boolean hasPermissions(String[] permissions) {
        int result;
        for (String perms: permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED)
                return false;
        }

        return true;
    }

    private void getPhotoData() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        requestActivity.launch(intent);
    }

    /*
    External APIs
     */
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {                     // Permission Not Granted
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {                                                // Permission Already Granted
                getPhotoData();
            }
        } else {                                                    // Permission Not Necessary
            getPhotoData();
        }
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