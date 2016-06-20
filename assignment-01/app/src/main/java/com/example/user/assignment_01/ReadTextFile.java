package com.example.user.assignment_01;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Download the text file to the phone internal cache storage.
 * Copy its content to read.txt that has already been created.
 * Get the content of read.txt and display in a EditText
 */
public class ReadTextFile extends AsyncTask<Void, Void, Boolean>{
    private Context context;
    private String filePath;
    private String localTempFilePath;

    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;
    private BufferedReader bufferedReader;

    static StringBuilder fileContent;
    private String line;

    public ReadTextFile(Context context, String filePath){
        this.context = context;
        this.filePath = filePath;
        this.localTempFilePath = context.getCacheDir().getAbsolutePath() + "/" + "read.txt";
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Log.i("Local File", localTempFilePath);

            File localTempFile = new File(localTempFilePath);

            fileOutputStream = new FileOutputStream(localTempFile);
            DropboxAPI.DropboxFileInfo info = DataStorage.mApi.getFile(filePath, null, fileOutputStream, null);

            fileInputStream = new FileInputStream(localTempFile);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            fileContent = new StringBuilder();
            int i = 1;
            while((line = bufferedReader.readLine()) != null){
                Log.i("Read Line", i + " " + line);
                fileContent.append(line + "\n");
                i = i + 1;
            }
            fileInputStream.close();

            Log.i("ReadTextFile", "OK");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void onPostExecute(Boolean result){
        if(true) {
            DataStorage.showNoti(context, "File is opened");
            OpenTextFileDialog.textEditor.setText(ReadTextFile.fileContent.toString());
        }else
            DataStorage.showNoti(context, "Cannot open the file.");
    }
}
