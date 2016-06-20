package com.example.user.assignment_01;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.exception.DropboxException;

/**
 * Created by User on 26-Nov-15.
 */
public class RenameItem extends AsyncTask<Void, Void, Boolean>{
    private Context context;
    private String parentPath;
    private String currentName;
    private String newName;

    ProgressDialog progressDialog;

    public RenameItem(Context context, String parentPath, String currentName, String newName){
        this.context = context;
        this.parentPath = parentPath;
        this.currentName = currentName;
        this.newName = newName;

        // Set up a progress dialog
        progressDialog = DataStorage.createProgressDialog(context, progressDialog, "Renaming an item");
        // Display the progress dialog
        progressDialog.show();
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Log.i("Parent path", parentPath);
            Log.i("Rename", "Old path: " + parentPath + currentName);
            Log.i("Rename", "New path" + parentPath + newName);
            DataStorage.mApi.move(parentPath + currentName, parentPath + newName);
            return true;
        } catch (DropboxException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void onPostExecute(Boolean result){
        progressDialog.dismiss();
        if(result)
            DataStorage.showNoti(context, "Renamed!");
        else
            DataStorage.showNoti(context, "Error in the process.");
        DataStorage.returnToMainScreen(context);
    }
}
