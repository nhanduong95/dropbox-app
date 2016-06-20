package com.example.user.assignment_01;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;

/*
 * Display the DropBox filesystem along with its controllers (view mode, edit, delete, ...)
 */
public class MainActivity extends FragmentActivity {

    private Boolean isFirstLevel = true;

    private ListView aListView;
    private GridView aGridView;
    private LinearLayout plainScreen;

    private TextView goBackIcon;
    private TextView uploadIcon;
    private TextView listViewIcon;
    private TextView gridViewIcon;

    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // Hide the title bar
        super.onCreate(savedInstanceState);

        // Check if the user has been authenticated
        if (DataStorage.mApi != null && DataStorage.mApi.getSession().isLinked()){
            setContentView(R.layout.main_screen);

            aListView = (ListView) findViewById(R.id.a_list_view);
            aGridView = (GridView) findViewById(R.id.a_grid_view);
            plainScreen = (LinearLayout) findViewById((R.id.plain_screen));

            goBackIcon = (TextView) findViewById(R.id.go_back);
            uploadIcon = (TextView) findViewById(R.id.upload);
            listViewIcon = (TextView) findViewById(R.id.list_view_display);
            gridViewIcon = (TextView) findViewById(R.id.grid_view_display);

            exitButton = (Button) findViewById(R.id.exit);

            // Import FontAwesome and set the imported for the TextViews
            Typeface fontAwesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.woff");
            goBackIcon.setTypeface(fontAwesome);
            uploadIcon.setTypeface(fontAwesome);
            listViewIcon.setTypeface(fontAwesome);
            gridViewIcon.setTypeface(fontAwesome);

            Log.i("ChosenFolder", "" + (DataStorage.clickedFoldersInMainScreen.size() - 1));
            Log.i("1stLevel", isFirstLevel + "");
            if(DataStorage.clickedFoldersInMainScreen.size() - 1 < 0) {
                // Load and display items in root folder
                isFirstLevel = true;
                setView(this, "/");
            }else {
                // This allows the app to continue the screen where it had left to perform a certain
                // function (e.g. download, upload, rename, etc.)
                Log.i("Load View", DataStorage.clickedFoldersInMainScreen.
                                get(DataStorage.clickedFoldersInMainScreen.size() - 1));
                isFirstLevel = false;
                setView(this, DataStorage.clickedFoldersInMainScreen.
                        get(DataStorage.clickedFoldersInMainScreen.size() - 1));
            }
            // Set UI for touch motion
            exitButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        // When button is pressed, its UI changes
                        exitButton.setTextColor(Color.parseColor("#B03128"));
                        exitButton.setTypeface(Typeface.DEFAULT_BOLD);
                        exitButton.setBackgroundColor(Color.parseColor("#FCFDFB"));
                    }
                    if (e.getAction() == MotionEvent.ACTION_UP) {
                        // Finger was lifted, button's UI is back to normal
                        exitButton.setTextColor(Color.parseColor("#FCFDFB"));
                        exitButton.setTypeface(Typeface.DEFAULT);
                        exitButton.setBackgroundColor(Color.parseColor("#B03128"));
                    }
                    return false;
                }
            });

            // Set actions for clicked Views
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOut();
                }
            });

            goBackIcon.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    // If there exists at least 2 folders that has been clicked to open the content
                    if(DataStorage.clickedFoldersInMainScreen.size() - 1 > 0){
                        isFirstLevel = false;
                        Log.i("GoBack-MainScreen", DataStorage.clickedFoldersInMainScreen.
                                get(DataStorage.clickedFoldersInMainScreen.size() - 2));
                        setView(MainActivity.this, DataStorage.clickedFoldersInMainScreen.
                                get(DataStorage.clickedFoldersInMainScreen.size() - 2));
                        DataStorage.clickedFoldersInMainScreen.
                                remove(DataStorage.clickedFoldersInMainScreen.size() - 1);
                    }
                    // If there exists only 1 folder that has been clicked to open the content
                    else if(DataStorage.clickedFoldersInMainScreen.size() - 1 == 0){
                        isFirstLevel = true;
                        DataStorage.clickedFoldersInMainScreen.
                                remove(DataStorage.clickedFoldersInMainScreen.size() - 1);
                        Log.i("GoBack-MainScreen", "/");
                        setView(MainActivity.this, "/");
                    }
                    else
                        Log.i("GoBack-MainScreen", "Problem");
                }
            });

            listViewIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataStorage.displayMode = "list";
                    // Check if there is at least 1 clicked folder
                    if(DataStorage.clickedFoldersInMainScreen.size() >= 1)
                        // If yes, display the items in the most recently clicked folder
                        setView(MainActivity.this, DataStorage.clickedFoldersInMainScreen.
                                get(DataStorage.clickedFoldersInMainScreen.size() - 1));
                    else
                        // If no, display the items in the root folder
                        setView(MainActivity.this, "/");
                }
            });

            gridViewIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataStorage.displayMode = "grid";
                    // Check if there is at least 1 clicked folder
                    if (DataStorage.clickedFoldersInMainScreen.size() >= 1)
                        // If yes, display the items in the most recently clicked folder
                        setView(MainActivity.this, DataStorage.clickedFoldersInMainScreen.
                                get(DataStorage.clickedFoldersInMainScreen.size() - 1));
                    else
                        // If no, display the items in the root folder
                        setView(MainActivity.this, "/");
                }
            });

            aListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // When an chosenDialogItem in list view is clicked
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Check if the clicked item exists.
                    // This condition helps to prevent the appearance of IndexOutOfBound Exception
                    // when an item in the GridView is clicked more than 1 time continuously.
                    if (LoadItemsForMainScreen.entryList.size() > 0 && LoadItemsForMainScreen.entryList.get(position) != null) {
                        // Get the clicked entry from ArrayList called entryList
                        DropboxAPI.Entry entry = LoadItemsForMainScreen.entryList.get(position);

                        if (entry.isDir) {
                            DataStorage.clickedFoldersInMainScreen.add(entry.path);
                            Log.i("Clicked Item Path", entry.path);
                            isFirstLevel = false;
                            setView(MainActivity.this, entry.path);
                        }
                    } else
                        showNoti("Please wait.");
                }
            });

            aGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // When an item in list view is clicked
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check if the clicked chosenDialogItem exists.
                // This condition helps to prevent the appearance of IndexOutOfBound Exception
                // when an item in the GridView is clicked more than 1 time continuously.
                if (LoadItemsForMainScreen.entryList.size() > 0 && LoadItemsForMainScreen.entryList.get(position) != null){

                    // Get the clicked entry from ArrayList called entryList
                    DropboxAPI.Entry entry = LoadItemsForMainScreen.entryList.get(position);

                    if (entry.isDir){
                        DataStorage.clickedFoldersInMainScreen.add(entry.path);
                        Log.i("Clicked Item Path", entry.path);
                        isFirstLevel = false;
                        setView(MainActivity.this, entry.path);
                    }
                }
                else
                    showNoti("Please wait.");
                }
            });

            aGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    // Check if the DropBox has any items stored
                    if (LoadItemsForMainScreen.entryList.get(position) != null) {
                        // Get the clicked entry from ArrayList called entryList
                        DropboxAPI.Entry entry = LoadItemsForMainScreen.entryList.get(position);

                        // Show the full name of a clicked item
                        showNoti(entry.fileName());
                    } else
                        Log.i("Item Name Display", "Problem");
                    return true;
                }
            });

            uploadIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                // Open the file explorer dialog that display the filesystem of android external storage
                // User has to choose a file in this storage to upload.
                public void onClick(View v) {
                    FragmentManager fragmentManager = getFragmentManager();
                    // If the number of clicked folders is 0,
                    // the folder intended to hold the uploaded file is the root folder
                    if(DataStorage.clickedFoldersInMainScreen.size() - 1 < 0){
                        FileExplorerDialog fileExplorerDialog = new FileExplorerDialog(MainActivity.this,
                                "/", "phone-filesystem");
                        fileExplorerDialog.show(fragmentManager, "File Explorer Dialog");
                    // Otherwise, the folder intended to hold the uploaded file is the most recently clicked
                    }else {
                        FileExplorerDialog fileExplorerDialog = new FileExplorerDialog(MainActivity.this,
                                DataStorage.clickedFoldersInMainScreen.
                                        get(DataStorage.clickedFoldersInMainScreen.size() - 1), "phone-filesystem");
                        fileExplorerDialog.show(fragmentManager, "File Explorer Dialog");
                    }
                }
            });
        }
        else{
            // Haven't been authenticated, return to the previous Activity
            Log.i("Authentication", "Have to do it first.");
            Intent intent = new Intent(this, Authentication.class);
            startActivity(intent);
        }
    }

    private void setView(Context context, String folderPath){

        if(LoadItemsForMainScreen.entryList == null){
            aGridView.setVisibility(View.GONE);
            aListView.setVisibility(View.GONE);
            plainScreen.setVisibility(View.VISIBLE);
        }
        // Set view if the items listed on the screen belongs to another folder
        if (!isFirstLevel)
            goBackIcon.setVisibility(View.VISIBLE);
        else
            goBackIcon.setVisibility(View.GONE);

        // Set view for list view mode
        if (DataStorage.displayMode.equals("list")){
            plainScreen.setVisibility(View.GONE);
            aGridView.setVisibility(View.GONE);
            aListView.setVisibility(View.VISIBLE);
            try{
                LoadItemsForMainScreen loadItemsForMainScreen = new LoadItemsForMainScreen(context,
                        folderPath, aListView, this);
                loadItemsForMainScreen.execute();
                Log.i("ListView Display Mode", "OK in main screen.");
            }
            catch(Exception e){
                Log.i("ListView Display Mode", "Problem in main screen.");
            }
        }

        // Set view for grid view mode
        if (DataStorage.displayMode.equals("grid")){
            plainScreen.setVisibility(View.GONE);
            aGridView.setVisibility(View.VISIBLE);
            aListView.setVisibility(View.GONE);
            try{
                LoadItemsForMainScreen loadItemsForMainScreen = new LoadItemsForMainScreen(context,
                        folderPath, aGridView, this);
                loadItemsForMainScreen.execute();
                Log.i("GridView Display Mode", "OK in main screen.");
            }
            catch(Exception e){
                Log.i("GridView Display Mode", "Problem in main screen.");
            }
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(Authentication.ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private void logOut(){
        // Remove credentials from the session
        DataStorage.mApi.getSession().unlink();

        // Clear our stored keys in SharedPreferences
        clearKeys();

        // Move back to the Authentication (Login) screen
        Intent intent = new Intent(this, Authentication.class);
        startActivity(intent);
    }

    private void showNoti(String mess){
        // Toast: a view containing a quick little message for the user
        // 1st arg: context (usually the Application or Activity object)
        // 2nd arg: the text to show
        // 3rd arg: the duration to display to message
        Toast noti = Toast.makeText(this, mess, Toast.LENGTH_LONG);
        noti.show();
    }

    void activateRenameFunction(Context context, String itemParentPath, String itemName, String itemType){
        FragmentManager fragmentManager = getFragmentManager();
        RenameDialog renameDialog = new RenameDialog(context, itemParentPath, itemName, itemType);
        renameDialog.show(fragmentManager, "Rename Dialog");
    }

    void showFileExplorerDialog (Context context, String fromPath, String itemName, String mode){
        FragmentManager fragmentManager = getFragmentManager();
        FileExplorerDialog fileExplorerDialog = new FileExplorerDialog(context, fromPath, itemName, mode);
        fileExplorerDialog.show(fragmentManager, "File Explorer Dialog");
    }

    void activateDeleteFunction(Context context, String itemPath){
        FragmentManager fragmentManager = getFragmentManager();
        DeleteDialog deleteDialog = new DeleteDialog(context, itemPath);
        deleteDialog.show(fragmentManager, "Delete Dialog");
    }

    void activateOpenFunction(Context context, String itemPath, String itemName){
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        OpenTextFileDialog openTextFileDialog = new OpenTextFileDialog(context, itemPath, itemName);
        openTextFileDialog.show(fragmentManager, "Open Text File");
    }
}


