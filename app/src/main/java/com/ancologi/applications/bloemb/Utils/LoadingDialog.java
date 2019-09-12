package com.ancologi.applications.bloemb.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.ancologi.applications.bloemb.R;


public class LoadingDialog {

    private Dialog mDialog;

    private LoadingDialog(Activity mActivity) {
        mDialog = new Dialog(mActivity);
        mDialog.setContentView(R.layout.loading_dialog_layout);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setCancelable(false);
    }

    public static LoadingDialog getInstance(Activity mActivity) {
        return new LoadingDialog(mActivity);
    }

    public void showDialog() {
        mDialog.show();
    }

    public void closeDialog() {
        mDialog.dismiss();
    }
}
