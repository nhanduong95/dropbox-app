package com.example.user.assignment_01;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.exception.DropboxException;


public class MoveItem extends AsyncTask<String, Void, Boolean> {
    private Context context;

    String toPath;
    String fromPath;
    String sourceFileName;
    ProgressDialog progressDialog;

    public MoveItem(Context context, String fromPath, String toPath, String sourceFileName) {
        this.context = context;
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.sourceFileName = sourceFileName;

        // Set up a progress dialog
        progressDialog = DataStorage.createProgressDialog(context, progressDialog, "Moving an item");
        // Display the progress dialog
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            Log.i("Move source", fromPath);
            Log.i("Move destination", toPath + "/" + sourceFileName);

            DataStorage.mApi.move(fromPath, toPath + "/" + sourceFileName);

            Log.i("Move", "OK");
            return true;

        } catch (DropboxException e) {
            Log.i("Move", "Problem");
            return false;
        }
    }

    protected void onPostExecute(Boolean result){
        progressDialog.dismiss();
        if(result)
            DataStorage.showNoti(context, "Moved!");
        else
            DataStorage.showNoti(context, "Error in the process.");

        DataStorage.returnToMainScreen(context);
    }
}
