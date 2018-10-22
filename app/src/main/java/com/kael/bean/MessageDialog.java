package com.kael.bean;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.kael.mikrotik.R;

/**
 * Created by kael on 20/08/2016.
 */


public class MessageDialog {
    private Context context;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;

    public MessageDialog(Context context){
        this.context =context;
        this.progressDialog =new ProgressDialog(context);
        this.builder =new AlertDialog.Builder(context);
    }

    public void showProgressBar(String title, String message){
        progressDialog.setMessage(message);
        progressDialog.setTitle(title);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public AlertDialog.Builder getAlertDialog(String title, String message){
        builder.setTitle(title);
        builder.setMessage(message);
        //builder.setIcon(R.id.icon);
        return builder;
    }

    public void showAlertDialog(String title, String message, Boolean status) {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(context);

        alertDialog.setTitle(title);

        alertDialog.setMessage(message);

        alertDialog.setIcon((status) ? R.drawable.ic_launcher : R.drawable.error);

        alertDialog.setPositiveButton("Aceptar", null);

        alertDialog.show();
    }



    public void closeProgressBar(){
        progressDialog.dismiss();
    }
}
