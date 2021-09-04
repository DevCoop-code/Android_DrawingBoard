package com.hankyo.jeong.drawingboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hankyo.jeong.drawingboard.databinding.PaintingMainBinding;
import com.hankyo.jeong.drawingboard.utils.DialogCallback;
import com.hankyo.jeong.drawingboard.utils.Utils;
import com.hankyo.jeong.drawingboard.views.ImageResizeView;
import com.hankyo.jeong.drawingboard.views.PaintingToolElementAdapter;
import com.hankyo.jeong.drawingboard.views.PaintingToolListDecoration;
import com.hankyo.jeong.drawingboard.views.PaintingView;

import java.util.ArrayList;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaintActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "PaintActivity";

    private static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE"};

    private PaintingMainBinding binding;

    private PaintingView paintingView;

    private ImageButton doPaintColorBtn;
    private ImageButton doPaintToolBtn;

    ActivityResultLauncher<Intent> requestActivity;

    // ImagePhoto Move Value
    float oldXvalue, oldYvalue;
    float targetX = 0, targetY = 0;
    float drawX = 0, drawY = 0;
    int deltaX = 0, deltaY = 0;
    View targetImageView;

    // RecyclerView For Color Component
    private RecyclerView listview;
    private PaintingToolElementAdapter adapter;

    private ArrayList<String> colorItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = PaintingMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set UI Components
        setUI();

        // Set Color Pallette Components
        setColorPalletteListView();

        // Set ActivityResult Callback - For Photo Gallery
        requestActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    String photoImgPath = getImagePathFromURI(result.getData().getData());

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    Bitmap photoBitmap = BitmapFactory.decodeFile(photoImgPath, options);

                    ImageResizeView imageResizeView = new ImageResizeView(getApplicationContext());
                    imageResizeView.setTargetImage(photoBitmap);
                    imageResizeView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {

                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    targetImageView = view.findViewById(R.id.targetImage);
                                    deltaX = ((view.getWidth() - targetImageView.getWidth()) / 2);
                                    deltaY = (view.getHeight() - targetImageView.getHeight()) / 2;

                                    oldXvalue = motionEvent.getX();
                                    oldYvalue = motionEvent.getY();

                                    Log.d(TAG, "old Value X: " + oldXvalue + ", Y: " + oldYvalue);
                                    break;

                                case MotionEvent.ACTION_MOVE:
                                    targetX = motionEvent.getRawX() - deltaX - (targetImageView.getWidth() / 2);
                                    targetY = motionEvent.getRawY() - deltaY - (targetImageView.getHeight() / 2);

                                    view.setX(targetX);
                                    view.setY(targetY);
                                    break;

                                case MotionEvent.ACTION_UP:
                                    drawX = motionEvent.getRawX() - (targetImageView.getWidth() / 2);
                                    drawY = motionEvent.getRawY() - (targetImageView.getHeight() / 2);

                                    break;
                            }
                            return true;
                        }
                    });

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean externalStoragePermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!externalStoragePermissionAccepted) {       // Not Accepted
                        DialogCallback callback = new DialogCallback() {
                            @Override
                            public void willDoAcceptCallback() {
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                    requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                                }
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
                    } else {        // Accepted
                        getPhotoData();
                    }
                }
                break;
        }
    }

    /*
    Set UI Components
     */
    private void setUI() {
        if (binding != null) {
            paintingView = binding.paintView;
            listview = binding.colorListview;
        }
    }

    private void setColorPalletteListView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(PaintActivity.this, LinearLayoutManager.VERTICAL, false);
        if (listview != null) {
            listview.setLayoutManager(layoutManager);
            colorItemList.add("#000000");
            colorItemList.add("#FFFFFF");
            colorItemList.add("#ED1C24");
            adapter = new PaintingToolElementAdapter(PaintActivity.this, colorItemList, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = ((ViewGroup)view.getParent()).indexOfChild(view);
                    paintingView.setColor(colorItemList.get(index));
                }
            });
            listview.setAdapter(adapter);

            PaintingToolListDecoration decoration = new PaintingToolListDecoration();
            listview.addItemDecoration(decoration);
        }
    }

    private String getImagePathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imgPath = cursor.getString(idx);
            cursor.close();

            return imgPath;
        }
    }

    private void getPhotoData() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        requestActivity.launch(intent);
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

    /*
    Open API
     */
    public void changeColor(View view) {
        if (view != doPaintColorBtn) {
            String color = view.getTag().toString();
            paintingView.setColor(color);
            doPaintColorBtn = (ImageButton) view;
        }
    }

    public void changeTool(View view) {
        if (view != doPaintToolBtn) {
            String tool = view.getTag().toString();
            paintingView.setTool(tool);
            doPaintToolBtn = (ImageButton) view;
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
