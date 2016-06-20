package com.example.user.assignment_01;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadFile extends AsyncTask <String, Void, Boolean> {

    // AsyncTask has 3 main parameters: Params, Progress and Result
    // Params – the input. what you pass to the AsyncTask
    // Progress – if you have any updates, passed to onProgressUpdate()
    // Result – the output. what returns doInBackground()

    private Context context;
    private final ProgressDialog progressDialog;

    private String fileName;
    private String filePath;

    private boolean isCancelled;

    private FileOutputStream fileOutputStream;

    public DownloadFile(Context currentContext, String fileName, String filePath){
        // We set the context this way so we don't accidentally leak activities
        this.context = currentContext.getApplicationContext();
        this.fileName = fileName;
        this.filePath = filePath;

        // Create a progress dialog telling the user to wait for the process to finish
        progressDialog = new ProgressDialog(context);
        progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        progressDialog.setTitle("Downloading an item");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isCancelled = true;

                if(fileOutputStream != null){
                    try{
                        fileOutputStream.close();
                        progressDialog.dismiss();
                    }catch (IOException e){
                        DataStorage.showNoti(context, "Cancelled!");
                        Log.i("Download","Cancelled");
                    }
                }
            }
        });
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try{
            if (isCancelled)
                return false;
            // Create a file object based on a pathname absolute to the SDCard folder
            // Environment.getExternalStorageDirectory().getAbsolutePath() returns a absolute path to the SDCard
            File downloadFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName);
            Log.i("Download", "Create File object successful.");
            Log.i("Download file path", Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName);

            // Save the chosenDialogItem in the DropBox to another chosenDialogItem with the same name on the local filesystem
            fileOutputStream = new FileOutputStream(downloadFile);
            Log.i("Download", "FileOutputStream works OK.");

            // Retrieve the chosen chosenDialogItem in DropBox via getFile method
            // 4 parameters for getFile method
            // path - the DropBox path to the file.
            // rev - the revision (from the file's metadata) of the file to download, or null to get the latest version.
            // os - the OutputStream to write the file to.
            // listener - an optional ProgressListener to receive progress updates as the file downloads, or null.
            DropboxAPI.DropboxFileInfo info = DataStorage.mApi.getFile(filePath, null, fileOutputStream, null);
            Log.i("getFile method", "OK");

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            if(fileOutputStream != null){
                try{
                    fileOutputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    protected void onPostExecute(Boolean result){
        progressDialog.dismiss();
        if(result)
            DataStorage.showNoti(context, "Downloaded!");
        else if(isCancelled)
            DataStorage.showNoti(context, "Cancelled!");
        else
            DataStorage.showNoti(context, "Error in the process.");
    }
}
