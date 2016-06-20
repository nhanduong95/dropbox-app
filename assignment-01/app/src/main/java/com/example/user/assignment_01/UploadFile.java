package com.example.user.assignment_01;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UploadFile extends AsyncTask<Void, Void, Boolean> {
    private String filePath;    // Path of the intended upload file
    private String fileName;    // Name of the intended upload file
    private String dropboxParentFolderPath;

    private boolean isCancelled;

    private FileInputStream fileInputStream;
    private Context context;

    private final ProgressDialog progressDialog;

    public UploadFile(Context currentContext, String filePath, String fileName, String dropboxParentFolderPath){
        // We set the context this way so we don't accidentally leak activities
        this.context = currentContext.getApplicationContext();
        this.fileName = fileName;
        this.filePath = filePath;
        this.dropboxParentFolderPath = dropboxParentFolderPath;

        // Create a progress dialog telling the user to wait for the process to finish
        progressDialog = new ProgressDialog(context);
        progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        progressDialog.setTitle("Uploading an item");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isCancelled = true;

                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                        progressDialog.dismiss();
                    } catch (IOException e) {
                        DataStorage.showNoti(context, "Cancelled!");
                        Log.i("Download", "Cancelled");
                    }
                }
            }
        });
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try{
            File uploadFile = new File(filePath);
            fileInputStream = new FileInputStream(uploadFile);
            Log.i("Upload", "Phone full path: " + filePath);
            Log.i("Upload", "DropBox full path: " + dropboxParentFolderPath + "/" + fileName);
            DropboxAPI.Entry entry = DataStorage.mApi.putFile(dropboxParentFolderPath + "/" + fileName,
                    fileInputStream, uploadFile.length(), null, null);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(fileInputStream != null){
                try{
                    if(fileInputStream != null)
                        fileInputStream.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    protected void onPostExecute(Boolean result){
        progressDialog.dismiss();
        if(result)
            DataStorage.showNoti(context, "Uploaded!");
        else if(isCancelled)
            DataStorage.showNoti(context, "Cancelled!");
        else
            DataStorage.showNoti(context, "Error in the process.");
        DataStorage.returnToMainScreen(context);
    }
}