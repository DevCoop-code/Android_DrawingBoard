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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hankyo.jeong.drawingboard.databinding.ActivityMainBinding;
import com.hankyo.jeong.drawingboard.utils.DialogCallback;
import com.hankyo.jeong.drawingboard.utils.Utils;
import com.hankyo.jeong.drawingboard.views.PaintingView;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";

    private static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE"};

    private ActivityMainBinding binding;

    private PaintingView paintingView;
    private ImageButton currPaint;
    private ImageButton currTool;

    ActivityResultLauncher<Intent> requestActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

            }
        } else {                                                    // Permission Not Necessary

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