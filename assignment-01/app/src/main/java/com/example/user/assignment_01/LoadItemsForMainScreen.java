package com.example.user.assignment_01;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.google.common.io.Files;

import java.util.ArrayList;

/*
 * Fetch the filesystem from DropBox and load it into the main screen by ViewAdapterForMainScreen
 */

public class LoadItemsForMainScreen extends AsyncTask<String, Void, ArrayList<Item>> {
    // AsyncTask has 3 main parameters: Params, Progress and Result
    // Params – the input. what you pass to the AsyncTask
    // Progress – if you have any updates, passed to onProgressUpdate()
    // Result – the output. what returns doInBackground()
    private Context currentContext;
    private String itemPath;
    private ListView listView;
    private GridView gridView;

    private MainActivity mainActivity;

    static ArrayList<DropboxAPI.Entry> entryList;

    public LoadItemsForMainScreen(Context currentContext, String itemPath,
                                  ListView listView, MainActivity mainActivity){
        // We set the context this way so we don't accidentally leak activities
        this.currentContext = currentContext.getApplicationContext();
        this.itemPath = itemPath;
        this.listView = listView;

        this.mainActivity = mainActivity;
    }

    public LoadItemsForMainScreen(Context currentContext, String itemPath,
                                  GridView gridView, MainActivity mainActivity){
        this.currentContext = currentContext;
        this.itemPath = itemPath;
        this.gridView = gridView;

        this.mainActivity = mainActivity;
    }

    @Override
    protected ArrayList<Item> doInBackground(String... params){
        entryList = new ArrayList<>();
        ArrayList<Item> dirItemList = new ArrayList<>();
        try {
            // Returns the metadata for a file, or for a folder and (optionally) its immediate children,
            // depending on what is the path here

            // 1st arg - String path: the DropBox path to the file or folder for which to get metadata.

            // 2nd arg - int fileLimit: the maximum number of children to return for a folder.
            // 2nd arg: Pass in 1 if getting metadata for a file.

            // 3rd arg - String hash: hash of the previously stored metadata that belongs to a folder.
            // 3rd arg: Pass in null for files or unknown directories.

            // 4th arg - boolean list: if true, returns metadata for a folder's immediate children.
            // 4th arg - boolean list: if false, just returns the folder itself and ignores the files.

            // 5th arg - String revision: gets metadata for a file at a prior revision (optional)
            // 5th arg: Use null for the latest metadata.
            DropboxAPI.Entry entry = DataStorage.mApi.metadata(itemPath, 1000, null, true, null);

            // entry.contents returns a list of immediate children in a folder
            // Use loop to add all the entries of a folder to an ArrayList
            for (DropboxAPI.Entry e: entry.contents)
                entryList.add(e);

            Log.i("NoOfItems", "Number of items in a view: " + entryList.size());

            // Use loop to fill data into an ArrayList of Item objects
            for (DropboxAPI.Entry e: entryList){
                if(e != null){
                    dirItemList.add(new Item(e.fileName(), e.path));
                    Log.i("FileName", "No: " + entryList.indexOf(e) + " |Name: " + e.fileName());
                    // if entry is a folder
                    if (e.isDir) {
                        // Set the icon for folder entry
                        dirItemList.get(entryList.indexOf(e)).setIconId(R.string.icon_folder_open);

                        // Set the item type for the entry (will be used in setting the function dialog)
                        dirItemList.get(entryList.indexOf(e)).setItemType("folder");

                    // if entry is a text file
                    } else if (Files.getFileExtension(e.path).equals("txt")){
                        // Set the icon for text file entry
                        dirItemList.get(entryList.indexOf(e)).setIconId(R.string.icon_file_text);

                        dirItemList.get(entryList.indexOf(e)).setItemType("text-file");
                    } else if (Files.getFileExtension(e.path).matches("png|jpg|jpeg|PNG|JPG|JPEG")) {
                        // Set the icon for image file entry
                        dirItemList.get(entryList.indexOf(e)).setIconId(R.string.icon_img);

                        dirItemList.get(entryList.indexOf(e)).setItemType("image-file");
                    }else {
                        // Set the item type for the entry (will be used in setting the function dialog)
                        dirItemList.get(entryList.indexOf(e)).setItemType("file");

                        if (Files.getFileExtension(e.path).matches("ppt|pptx|pptm"))
                            // Set the icon for the powerpoint file entry
                            dirItemList.get(entryList.indexOf(e)).setIconId(R.string.icon_file_powerpoint);

                        else if (Files.getFileExtension(e.path).matches("ar|mar|iso|zip|rar|7z|tar|gz|xz|lz|z"))
                            // Set the icon for the archive file entry
                            dirItemList.get(entryList.indexOf(e)).setIconId(R.string.icon_file_archive);

                        else if (Files.getFileExtension(e.path).matches("xlsx|xlsm|xlsb|xltx|xltm|xls|xlt|xls|xml|xlam|xla|xlw"))
                            // Set the icon for the excel file entry
                            dirItemList.get(entryList.indexOf(e)).setIconId(R.string.icon_file_excel);

                        else if (Files.getFileExtension(e.path).equals("pdf"))
                            // Set the icon for the pdf file entry
                            dirItemList.get(entryList.indexOf(e)).setIconId(R.string.icon_file_pdf);

                        else if (Files.getFileExtension(e.path).matches("doc|docx"))
                            // Set the icon for the word file entry
                            dirItemList.get(entryList.indexOf(e)).setIconId(R.string.icon_file_word);

                        else
                            // Set the icon for the file entry in general
                            dirItemList.get(entryList.indexOf(e)).setIconId(R.string.icon_file);
                    }
                    dirItemList.get(entryList.indexOf(e)).setItemParentPath(e.parentPath());
                }
            }
        } catch (DropboxException e) {
            e.printStackTrace();
        }
        return dirItemList;
    }

    @Override
    protected void onPostExecute(ArrayList<Item> itemList){
        super.onPostExecute(itemList);
        try{
            // Display folder items in List View mode
            if(DataStorage.displayMode.equals("list")){
                ViewAdapterForMainScreen listViewAdapterForMainScreen = new ViewAdapterForMainScreen(currentContext,
                        R.layout.list_row_main_screen, itemList, mainActivity);
                listView.setAdapter(listViewAdapterForMainScreen);
            }
            // Display folder items in Grid View mode
            else{
                ViewAdapterForMainScreen gridViewAdapterForMainScreen = new ViewAdapterForMainScreen(currentContext,
                        R.layout.grid_row, itemList, mainActivity);
                gridView.setAdapter(gridViewAdapterForMainScreen);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}