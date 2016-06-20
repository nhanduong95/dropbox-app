package com.example.user.assignment_01;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DeleteItem extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private String itemPath;
    private ProgressDialog progressDialog;

    public DeleteItem(Context context, String itemPath){
        this.context = context;
        this.itemPath = itemPath;

        progressDialog = DataStorage.createProgressDialog(context, progressDialog, "Deleting an item");
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try{
            DataStorage.mApi.delete(itemPath);
            Log.i("Delete", "OK");
            return true;
        }catch (Exception e){
            Log.i("Delete", "Problem");
            return false;
        }
    }

    protected void onPostExecute(Boolean result){
        progressDialog.dismiss();
        if(result)
            DataStorage.showNoti(context, "Deleted!");
        else
            DataStorage.showNoti(context, "Error in the process.");
        DataStorage.returnToMainScreen(context);
    }
}
