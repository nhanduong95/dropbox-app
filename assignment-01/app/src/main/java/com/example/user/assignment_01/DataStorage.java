package com.example.user.assignment_01;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.util.ArrayList;

/*
 * Store commonly used static data for easier use across different classes
 */
public class DataStorage {
    static String displayMode = "list";
    static String clickedItemName;  // the item that has been clicked to trigger a dialog later

    // This ArrayList stores the path of all the clicked folders
    // for further use in navigation between filesystem levels.
    // Each folder represents a level in the filesystem
    static ArrayList<String> clickedFoldersInMainScreen = new ArrayList<>();;
    static DropboxAPI<AndroidAuthSession> mApi;

    public static ProgressDialog createProgressDialog(Context context, ProgressDialog progressDialog, String title){
        progressDialog = new ProgressDialog(context);
        progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        progressDialog.setTitle(title);
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }

    public static void showNoti(Context context, String message) {
        // Toast: a view containing a quick little message for the user
        // 1st arg: context (usually the Application or Activity object)
        // 2nd arg: the text to show
        // 3rd arg: the duration to display to message
        Toast noti = Toast.makeText(context, message, Toast.LENGTH_LONG);
        noti.show();
    }

    public static void returnToMainScreen(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
