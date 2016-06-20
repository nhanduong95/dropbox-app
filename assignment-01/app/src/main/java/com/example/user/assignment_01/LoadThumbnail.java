package com.example.user.assignment_01;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.dropbox.client2.DropboxAPI;
import com.google.common.io.Files;

import java.io.FileOutputStream;

/*
 * Thumbnail loading process for png/jp(e)g file only
 */
public class LoadThumbnail extends AsyncTask<Void, Void, Boolean> {
    private String imageFilePath;
    private String cachePathForPNG;
    private String cachePathForJPG;

    private Context context;
    private FileOutputStream fileOutputStream;

    private ImageView fileThumbnail;
    private Drawable imageGraphics;

    public LoadThumbnail(Context context, String imageFilePath, ImageView fileThumbnail){
        this.context = context.getApplicationContext();
        this.imageFilePath = imageFilePath;
        this.fileThumbnail = fileThumbnail;
        if(Files.getFileExtension(imageFilePath).equalsIgnoreCase("png"))
            cachePathForPNG = context.getCacheDir().getAbsolutePath() + "/" + "temp.png";
        else
            cachePathForJPG = context.getCacheDir().getAbsolutePath() + "/" + "temp.jpg";
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        try{
            if(Files.getFileExtension(imageFilePath).equals("png")) {
                fileOutputStream = new FileOutputStream(cachePathForPNG);
                DataStorage.mApi.getThumbnail(imageFilePath, fileOutputStream,
                        DropboxAPI.ThumbSize.ICON_256x256,
                        DropboxAPI.ThumbFormat.PNG, null);
                imageGraphics = Drawable.createFromPath(cachePathForPNG);
            }else  {
                fileOutputStream = new FileOutputStream(cachePathForJPG);
                DataStorage.mApi.getThumbnail(imageFilePath, fileOutputStream,
                        DropboxAPI.ThumbSize.ICON_256x256,
                        DropboxAPI.ThumbFormat.JPEG, null);
                imageGraphics = Drawable.createFromPath(cachePathForJPG);
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    protected void onPostExecute(Boolean result){
        if(result) {
            Log.i("FileWithThumb", imageFilePath);
            fileThumbnail.setImageDrawable(imageGraphics);
        }else
            DataStorage.showNoti(context, "Cannot load thumbnail(s).");
    }
}
