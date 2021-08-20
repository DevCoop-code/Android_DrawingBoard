package com.hankyo.jeong.drawingboard.utils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;



public class Utils {

    private static DialogCallback dialogCallback;

    @TargetApi(Build.VERSION_CODES.M)
    public static void showDialogForPermission(Context context, String titleMsg, String yesMsg, String noMsg, String msg, DialogCallback callback) {
        dialogCallback = callback;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleMsg);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(yesMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogCallback.willDoAcceptCallback();
            }
        });
        builder.setNegativeButton(noMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogCallback.willDoDenyCallback();
            }
        });
        builder.create().show();
    }
}
