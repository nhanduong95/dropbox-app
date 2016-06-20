package com.example.user.assignment_01;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/*
 * Fetch the filesystem and load it into the dialog by ViewAdapterForDialog
 */
public class LoadItemsForDialog extends AsyncTask<String, Void, ArrayList<Item>> {
    private Context context;
    private String itemPath;
    private ListView listView;
    private boolean isNull;
    private String mode;

    static File[] items;
    private ArrayList<Item> itemList;

    static ArrayList<DropboxAPI.Entry> entryList;

    public LoadItemsForDialog(Context context, String itemPath, ListView listView, String mode){
        this.context = context;
        this.itemPath = itemPath;
        this.listView = listView;
        this.mode = mode;
    }

    @Override
    protected ArrayList<Item> doInBackground(String... params) {
        entryList = new ArrayList<>();
        itemList = new ArrayList<>();
        try {
            // Load DropBox filesystem
            if(mode.equals("dropbox-filesystem")){
                // Returns the metadata for a file, or for a folder and (optionally) its immediate children,
                // depending on what is the path here
                DropboxAPI.Entry entry = DataStorage.mApi.metadata(itemPath, 1000, null, true, null);

                if(entry != null) {
                    // entry.contents returns a list of immediate children in a folder
                    // Use loop to add all the entries of a folder to an ArrayList
                    for (DropboxAPI.Entry e : entry.contents){
                        if(e != null && e.isDir)
                            entryList.add(e);
                    }
                    isNull = false;
                }
                else
                    isNull = true;

                if(entryList.size() > 0){
                    // Use loop to fill data into an ArrayList of Item objects
                    for (DropboxAPI.Entry e: entryList){
                        if(e != null & !e.fileName().equals(DataStorage.clickedItemName))
                            itemList.add(new Item(e.fileName(), e.path, R.string.icon_folder_closed));
                    }
                    isNull = false;
                }
                else
                    isNull = true;

            // Load the filesystem of the phone's external storage
            }else {
                // Create a root folder to begin
                File chosenItem = new File(itemPath);
                chosenItem.mkdirs();

                // Check if the root folder has been created successfully
                if(chosenItem.exists()){
                    // Filter the appropriate items of the root folder
                    FilenameFilter filenameFilter = new FilenameFilter() {
                        @Override
                        public boolean accept(File folderPath, String itemName) {
                            File item = new File(folderPath, itemName);
                            // Only items that are not hidden is acceptable
                            if(!item.isHidden())
                                return true;
                            else
                                return false;
                        }
                    };

                    // Put all the items filtered above in an array
                    items = chosenItem.listFiles(filenameFilter);
                    // Add the items in an ArrayList with more associated attributes
                    for(File item: items){
                        if(item.isDirectory())
                            itemList.add(new Item(item.getName(), item.getAbsolutePath(), R.string.icon_folder_closed));
                        else
                            itemList.add(new Item(item.getName(), item.getAbsolutePath(), R.string.icon_file_white));
                    }
                }

            }
        } catch (DropboxException e) {
            e.printStackTrace();
        }
        return itemList;
    }

    @Override
    protected void onPostExecute(ArrayList<Item> folderList){
        super.onPostExecute(folderList);
        try{
            // Populate the adapter to the ListView
            ViewAdapterForDialog viewAdapterForDialog = new ViewAdapterForDialog(context, R.layout.file_explorer_dialog, folderList);
            listView.setAdapter(viewAdapterForDialog);
            Log.i("ViewAdapter", "OK in dialog");

            // If the filesystem is empty
            if(isNull)
                DataStorage.showNoti(context, "There is nothing more to show.");

        }catch (Exception e){
            Log.i("ViewAdapter", "Problem in dialog");
            DataStorage.showNoti(context, "There's an error in displaying the folders.");
        }
    }
}
