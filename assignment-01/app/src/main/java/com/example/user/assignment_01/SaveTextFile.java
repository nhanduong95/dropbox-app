package com.example.user.assignment_01;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

/**
 * Write the content of the EditText shown in the editor
 * to a temporary file stored in the phone's internal cache storage called write.txt
 * Upload this file to the DropBox
 * Overwrite the DropBox text file that you chose to edit earlier
 */

public class SaveTextFile extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private String localTempFilePath;
    private String editedTextFile;
    private String fileContent;

    private FileInputStream fileInputStream;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;

    private ProgressDialog progressDialog;

    public SaveTextFile(Context context, String editedTextFile, String fileContent){
        this.context = context;
        this.editedTextFile = editedTextFile;
        this.fileContent = fileContent;
        localTempFilePath = context.getCacheDir().getAbsolutePath() + "/" + "write.txt";
        progressDialog = DataStorage.createProgressDialog(context, progressDialog, "Saving file");
        progressDialog.show();

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            File localTempFile = new File(localTempFilePath);

            fileWriter = new FileWriter(localTempFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            String[] lines = fileContent.split("\n");
            for(int i = 0; i < lines.length; i++){
                bufferedWriter.write(lines[i]);
                bufferedWriter.newLine();
                Log.i("File Content", i + " " + lines[i]);
            }
            bufferedWriter.close();

            fileInputStream = new FileInputStream(localTempFile);

            DropboxAPI.Entry entry = DataStorage.mApi.putFileOverwrite(editedTextFile,
                    fileInputStream, localTempFile.length(), null);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void onPostExecute(Boolean result){
        progressDialog.dismiss();
        if(result)
            DataStorage.showNoti(context, "Saved!");
        else
            DataStorage.showNoti(context, "Cannot be saved!");
    }

}
