package com.example.user.assignment_01;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.util.ArrayList;

/**
 * Used for file browsing in Move and Upload function
 */
@SuppressLint("ValidFragment")
public class FileExplorerDialog extends DialogFragment {
    private Context context;

    private TextView chosenLocation;    //used to show the current folder path
    private TextView goBackIcon;
    private Button okButton;
    private Button cancelButton;
    private ListView listView;

    private String toPath;
    private String fromPath;
    private String clickedItemName;
    private String dropboxFolderPath;   //the destination where the intended upload file will be place
    private String mode;

    private File chosenItem;    //used for android external storage's filesystem only

    boolean isFirstLevel = true;

    // This ArrayList stores the path of all the clicked folders
    // for further use in navigation between filesystem levels.
    // Each folder represents a level in the filesystem
    ArrayList<String> chosenFolder = new ArrayList<>();

    @SuppressLint("ValidFragment")
    // Used for DropBox filesystem (in move function)
    public FileExplorerDialog(Context context, String fromPath, String clickedItemName, String mode){
        this.context = context.getApplicationContext();
        this.mode = mode;
        this.fromPath = fromPath;
        this.clickedItemName = clickedItemName;

        DataStorage.clickedItemName = clickedItemName;
    }

    // Used for android external storage's filesystem (in upload function)
    public FileExplorerDialog(Context context, String dropboxFolderPath, String mode){
        this.context = context.getApplicationContext();
        this.dropboxFolderPath = dropboxFolderPath;
        this.mode = mode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.file_explorer_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        chosenLocation = (TextView) view.findViewById(R.id.chosen_location_name_display);
        goBackIcon = (TextView) view.findViewById(R.id.move_go_back);
        okButton = (Button) view.findViewById(R.id.move_ok);
        cancelButton = (Button) view.findViewById(R.id.move_cancel);
        listView = (ListView) view.findViewById(R.id.move_file_display);

        // Import FontAwesome and set the imported for the TextView
        Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.woff");
        goBackIcon.setTypeface(fontAwesome);

        if(mode.equals("dropbox-filesystem")){
            setView("/", mode);   // Load and display items in root folder
            chosenLocation.setText("/");
            this.toPath = chosenLocation.getText().toString();
        }else{
            setView(Environment.getExternalStorageDirectory() + "", mode);   // Load and display items in root folder
            chosenLocation.setText(Environment.getExternalStorageDirectory() + "");
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // In DropBox filesystem, only folders are shown so
                if(mode.equals("dropbox-filesystem")){
                    if (LoadItemsForDialog.entryList.size() > 0 &&
                            LoadItemsForDialog.entryList.get(position) != null) {
                        // Get the actual entry for the clicked folder
                        DropboxAPI.Entry entry = LoadItemsForDialog.entryList.get(position);

                        // Show the clicked folder's path in chosenLocation TextView
                        chosenLocation.setText(entry.path);

                        // Reset the destination path (folder's path)
                        // to which the chosen item will be moved
                        toPath = chosenLocation.getText().toString();


                        // add the clicked item name to chosenFolder ArrayList
                        // for further use with the go-back button
                        chosenFolder.add(entry.path);
                        Log.i("Clicked Item Path", entry.path);

                        // Reset the level of the filesystem
                        isFirstLevel = false;

                        // Set the view for the filesystem
                        setView(entry.path, mode);
                    }
                // In android external storage, files and folders are shown so
                }else{
                    if(LoadItemsForDialog.items.length > 0 &&
                            LoadItemsForDialog.items[position] != null){
                        chosenItem = LoadItemsForDialog.items[position];

                        // Show the clicked item's path in chosenLocation TextView
                        chosenLocation.setText(chosenItem.getPath());

                        if(chosenItem.isDirectory()) {
                            chosenFolder.add(chosenItem.getAbsolutePath());
                            Log.i("Clicked Item Path", chosenItem.getAbsolutePath());

                            // Reset the level of the filesystem
                            isFirstLevel = false;

                            // Set the view for the filesystem
                            setView(chosenItem.getAbsolutePath(), mode);
                        }
                    }
                }

            }
        });

        goBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If there exists at least 2 folders that has been clicked
                if (chosenFolder.size() - 1 > 0) {
                    isFirstLevel = false;
                    Log.i("GoBack-Dialog", chosenFolder.get(chosenFolder.size() - 2));
                    setView(chosenFolder.get(chosenFolder.size() - 2), mode);
                    chosenLocation.setText(chosenFolder.get(chosenFolder.size() - 2));
                    if(mode.equals("dropbox-filesystem"))
                        toPath = chosenLocation.getText().toString();
                    chosenFolder.remove(chosenFolder.size() - 1);
                }
                // If there exists only 1 folder that has been clicked
                else if (chosenFolder.size() - 1 == 0) {
                    isFirstLevel = true;
                    chosenFolder.remove(chosenFolder.size() - 1);
                    if(mode.equals("dropbox-filesystem")){
                        Log.i("GoBack-Dialog", "/");
                        setView("/", mode);
                        chosenLocation.setText("/");
                        toPath = chosenLocation.getText().toString();
                    }else{
                        Log.i("GoBack-Dialog", Environment.getExternalStorageDirectory() + "");
                        setView(Environment.getExternalStorageDirectory() + "", mode);
                        chosenLocation.setText(Environment.getExternalStorageDirectory() + "");
                    }
                } else
                    Log.i("GoBack-Dialog", "Problem");
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to the main screen
                dismiss();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode.equals("dropbox-filesystem")){
                    //Execute moving process
                    MoveItem moveProcess = new MoveItem(context, fromPath, toPath, clickedItemName);
                    moveProcess.execute();
                }
                else{
                    if(chosenItem.isDirectory())
                        DataStorage.showNoti(context, "Please choose a file to upload.");
                    else {
                        //Execute upload process
                        UploadFile uploadProcess = new UploadFile(context,
                                chosenItem.getAbsolutePath(), chosenItem.getName(), dropboxFolderPath);
                        uploadProcess.execute();
                    }
                }
            }
        });
        return view;
    }
    public void setView(String folderPath, String mode){
        if(isFirstLevel){
            goBackIcon.setVisibility(View.GONE);
            if(mode.equals("dropbox-filesystem")) {
                LoadItemsForDialog loadItemsForDialog = new LoadItemsForDialog(context, "/", listView, mode);
                loadItemsForDialog.execute();
            }else{
                LoadItemsForDialog loadItemsForDialog = new LoadItemsForDialog(context, Environment.getExternalStorageDirectory() + "", listView, mode);
                loadItemsForDialog.execute();
            }
        }else{
            goBackIcon.setVisibility(View.VISIBLE);
            LoadItemsForDialog loadItemsForDialog = new LoadItemsForDialog(context, folderPath, listView, mode);
            loadItemsForDialog.execute();
        }
    }
}
