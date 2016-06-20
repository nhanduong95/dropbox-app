package com.example.user.assignment_01;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.common.io.Files;

import java.util.ArrayList;

/*
 * Define a custom ArrayAdapter to bind data to ListView
 */
public class ViewAdapterForMainScreen extends ArrayAdapter<Item> {
    private Context context;
    private ArrayList<Item> list;
    private int resourceId;
    private MainActivity mainActivity;

    private Dialog dialog;

    public ViewAdapterForMainScreen(Context context, int resourceId,
                                    ArrayList<Item> list, MainActivity mainActivity){
        super(context, resourceId, list);

        // We set the context this way so we don't accidentally leak activities
        this.context = context.getApplicationContext();
        this.resourceId = resourceId;
        this.list = list;

        this.mainActivity = mainActivity;
    }

    static class ListViewHolder{
        // A static inner class for your adapter which holds references to the relevant views.
        // This one is for List View display mode

        // Without ListViewHolder, Android has to inflate an XML layout for every newly visible row,
        // which causes a Java object to be created and memory consumption.

        // With ViewHolder, the Java objects which represents the rows can be reused for newly visible rows
        // that are created when user scroll the list.
        TextView itemName;
        TextView itemIcon;
        TextView plusIcon;
        TextView clickedItemName;

        RadioGroup functionList;
        RadioButton viewFunction;
        RadioButton downloadFunction;
        RadioButton renameFunction;
        RadioButton moveFunction;
        RadioButton deleteFunction;

        Button cancelButton;
        Button okButton;
    }

    static class GridViewHolder{
        // Similar to ListViewHolder but this one is for List View display mode

        TextView itemName;
        TextView itemIcon;
        TextView cogIcon;
        TextView clickedItemName;

        RadioGroup functionList;
        RadioButton viewFunction;
        RadioButton downloadFunction;
        RadioButton renameFunction;
        RadioButton moveFunction;
        RadioButton deleteFunction;

        Button cancelButton;
        Button okButton;

        ImageView itemThumbnail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // If Android determines that a row is not visible anymore, it allows the getView() of the adapter method
        // to reuse the associated view via the convertView parameter.

        // The adapter can assign new data to the views contained in the view hierarchy of the convertView.
        // This avoids inflating an XML file and creating new Java objects to save memory.

        final ListViewHolder listViewHolder;
        final GridViewHolder gridViewHolder;
        // Adapter gets the data chosenDialogItem associated with the specified position in the data set.
        final Item item = getItem(position);

        // If the chosen display mode is List View
        if (DataStorage.displayMode.equals("list")){
            Log.i("ListItem", item.getItemName() + ": " + item.getItemType());

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // Inflate a new view hierarchy from the specified xml resource.
                // 1st arg: ID for an XML layout resource to load
                // 2nd arg: Optional parent view of the hierarchy
                convertView = inflater.inflate(R.layout.list_row_main_screen, null);

                // Configure listViewHolder
                listViewHolder = new ListViewHolder();
                listViewHolder.itemName = (TextView) convertView.findViewById(R.id.list_text_name);
                listViewHolder.itemIcon = (TextView) convertView.findViewById(R.id.list_item_icon);
                listViewHolder.plusIcon = (TextView) convertView.findViewById(R.id.plus_icon);

                // Import FontAwesome and set the imported for the TextView
                Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.woff");
                listViewHolder.itemIcon.setTypeface(fontAwesome);
                listViewHolder.plusIcon.setTypeface(fontAwesome);

                // Tag the ListViewHolder object with its relevant view
                convertView.setTag(listViewHolder);
            }
            else
                // Get the ListViewHolder object for the convertView
                listViewHolder = (ListViewHolder) convertView.getTag();

            // Fill data for the ListViewHolder
            listViewHolder.itemName.setText(item.getItemName());
            listViewHolder.itemIcon.setText(item.getIconId());

            // Add action for the plus icon
            // When it is clicked, a list of functions associated with the chosen chosenDialogItem is shown in form of a dialog
            listViewHolder.plusIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Set up the dialog
                    dialog = new Dialog(context); // Create a new dialog based on the context
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  // Hide the title bar
                    dialog.setContentView(R.layout.functions_dialog);   // Inflate the dialog with the user interface (XML file)

                    // Get the UI elements from the layout template (XML file) inflated in the dialog
                    listViewHolder.functionList = (RadioGroup) dialog.findViewById(R.id.functions);
                    listViewHolder.clickedItemName = (TextView) dialog.findViewById(R.id.clicked_item_name);
                    listViewHolder.viewFunction = (RadioButton) dialog.findViewById(R.id.view);
                    listViewHolder.downloadFunction = (RadioButton) dialog.findViewById(R.id.download);
                    listViewHolder.renameFunction = (RadioButton) dialog.findViewById(R.id.rename);
                    listViewHolder.moveFunction = (RadioButton) dialog.findViewById(R.id.move);
                    listViewHolder.deleteFunction = (RadioButton) dialog.findViewById(R.id.delete);

                    listViewHolder.okButton = (Button) dialog.findViewById(R.id.ok_btn);
                    listViewHolder.cancelButton = (Button) dialog.findViewById(R.id.cancel_btn);

                    // Set the name for the custom title bar
                    listViewHolder.clickedItemName.setText(item.getItemName());

                    // If the clicked item is a folder, download and edit function are gone
                    if (item.getItemType().equals("folder")) {
                        listViewHolder.downloadFunction.setVisibility(View.GONE);
                        listViewHolder.viewFunction.setVisibility(View.GONE);

                        // If the clicked item is a text file, download and edit function are visible
                    } else if (item.getItemType().equals("text-file")) {
                        listViewHolder.viewFunction.setVisibility(View.VISIBLE);
                        listViewHolder.downloadFunction.setVisibility(View.VISIBLE);

                        // If the clicked item is a normal file, download function is visible, edit function is gone
                    } else {
                        listViewHolder.downloadFunction.setVisibility(View.VISIBLE);
                        listViewHolder.viewFunction.setVisibility(View.GONE);
                    }

                    Log.i("Is Text File", item.getItemName() + " " + item.getItemType());
                    dialog.show();

                    // Set action for the cancel button in the dialog
                    // When button is clicked, the dialog disappears
                    listViewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    // Set action for the cancel button in the dialog
                    // When button is clicked, the dialog checks for the checked radio button
                    // and determines which operation to perform
                    listViewHolder.okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int selectedRadioButtonId = listViewHolder.functionList.getCheckedRadioButtonId();

                            // Find which radioButton is checked by id
                            if (selectedRadioButtonId == listViewHolder.viewFunction.getId())
                                // Open text file to view
                                mainActivity.activateOpenFunction(context, item.getItemPath(), item.getItemName());

                            else if (selectedRadioButtonId == listViewHolder.downloadFunction.getId()) {
                                // Download file
                                DownloadFile downloadProcess = new DownloadFile(context,
                                        item.getItemName(), item.getItemPath());
                                downloadProcess.execute();

                            } else if (selectedRadioButtonId == listViewHolder.renameFunction.getId())
                                // Rename file or folder. Open the file explorer dialog to choose the destination first.
                                mainActivity.activateRenameFunction(context,
                                        item.getItemParentPath(), item.getItemName(), item.getItemType());

                            else if (selectedRadioButtonId == listViewHolder.moveFunction.getId())
                                // Move file or folder
                                mainActivity.showFileExplorerDialog(context, item.getItemPath(),
                                        item.getItemName(), "dropbox-filesystem");

                            else if (selectedRadioButtonId == listViewHolder.deleteFunction.getId())
                                // Delete file or folder
                                mainActivity.activateDeleteFunction(context, item.getItemPath());

                            else
                                // Notify the user if he/she clicked OK without choosing anything
                                DataStorage.showNoti(context, "Please choose a function.");
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
        else{
            Log.i("GridItem", item.getItemName() + ": " + item.getItemType());

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // Inflate a new view hierarchy from the specified xml resource.
                // 1st arg: ID for an XML layout resource to load
                // 2nd arg: Optional parent view of the hierarchy
                convertView = inflater.inflate(R.layout.grid_row, null);

                // Configure listViewHolder
                gridViewHolder = new GridViewHolder();
                gridViewHolder.itemName = (TextView) convertView.findViewById(R.id.grid_item_name);
                gridViewHolder.itemIcon = (TextView) convertView.findViewById(R.id.grid_item_icon);
                gridViewHolder.cogIcon = (TextView) convertView.findViewById(R.id.grid_setting_icon);
                gridViewHolder.itemThumbnail = (ImageView) convertView.findViewById(R.id.grid_item_thumbnail);

                // Import FontAwesome and set the imported for the TextView
                Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.woff");
                gridViewHolder.itemIcon.setTypeface(fontAwesome);
                gridViewHolder.cogIcon.setTypeface(fontAwesome);

                // Set thumbnail to be gone if the item is not an image
                if(!item.getItemType().equals("image-file")) {
                    gridViewHolder.itemThumbnail.setVisibility(View.GONE);
                    Log.i("Thumbnail", "Gone");
                }else {
                    // Set the type icon to be gone if the item is an image
                    gridViewHolder.itemIcon.setVisibility(View.GONE);
                    Log.i("Icon", "Gone");

                    // Load thumbnail
                    LoadThumbnail loadThumbnailProcess = new LoadThumbnail(context,
                            item.getItemPath(), gridViewHolder.itemThumbnail);
                    loadThumbnailProcess.execute();
                }
                // Tag the GridViewHolder object with its relevant view
                convertView.setTag(gridViewHolder);
            }
            else
                // Get the GridViewHolder object for the convertView
                gridViewHolder = (GridViewHolder) convertView.getTag();

            // Fill data for the GridViewHolder
            if ((context.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) ==
                    Configuration.SCREENLAYOUT_SIZE_SMALL) {
                if(item.getItemName().length() <= 18)
                    // Show the item full name if the name has at most 31 character.
                    gridViewHolder.itemName.setText(item.getItemName());
                else
                    // Otherwise, it has to be trimmed to 15 character and added with "..." afterwards
                    gridViewHolder.itemName.setText(item.getItemName().substring(0, 15) + "...");
                Log.i("LARGE VIEW", "Ok");
            }else if ((context.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) ==
                    Configuration.SCREENLAYOUT_SIZE_NORMAL){
                if(item.getItemName().length() <= 18)
                    // Show the item full name if the name has at most 31 character.
                    gridViewHolder.itemName.setText(item.getItemName());
                else
                    // Otherwise, it has to be trimmed to 15 character and added with "..." afterwards
                    gridViewHolder.itemName.setText(item.getItemName().substring(0, 10) + "...");
                Log.i("NORMAL VIEW", "Ok");
            }else if ((context.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) ==
                    Configuration.SCREENLAYOUT_SIZE_SMALL){
                if(item.getItemName().length() <= 18)
                    // Show the item full name if the name has at most 31 character.
                    gridViewHolder.itemName.setText(item.getItemName());
                else
                    // Otherwise, it has to be trimmed to 15 character and added with "..." afterwards
                    gridViewHolder.itemName.setText(item.getItemName().substring(0, 5) + "...");
                Log.i("SMALL VIEW", "Ok");
            }

            gridViewHolder.itemIcon.setText(item.getIconId());
            gridViewHolder.cogIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Set up the dialog
                    dialog = new Dialog(context); // Create a new dialog based on the context
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  // Hide the title bar
                    dialog.setContentView(R.layout.functions_dialog);   // Inflate the dialog with the user interface (XML file)

                    // Get the UI elements from the layout template (XML file) inflated in the dialog
                    gridViewHolder.functionList = (RadioGroup) dialog.findViewById(R.id.functions);
                    gridViewHolder.clickedItemName = (TextView) dialog.findViewById(R.id.clicked_item_name);
                    gridViewHolder.viewFunction = (RadioButton) dialog.findViewById(R.id.view);
                    gridViewHolder.downloadFunction = (RadioButton) dialog.findViewById(R.id.download);
                    gridViewHolder.renameFunction = (RadioButton) dialog.findViewById(R.id.rename);
                    gridViewHolder.moveFunction = (RadioButton) dialog.findViewById(R.id.move);
                    gridViewHolder.deleteFunction = (RadioButton) dialog.findViewById(R.id.delete);

                    gridViewHolder.okButton = (Button) dialog.findViewById(R.id.ok_btn);
                    gridViewHolder.cancelButton = (Button) dialog.findViewById(R.id.cancel_btn);

                    // Set the name for the custom title bar
                    gridViewHolder.clickedItemName.setText(item.getItemName());

                    // If the clicked item is a folder, download and edit function are gone
                    if (item.getItemType().equals("folder")) {
                        gridViewHolder.downloadFunction.setVisibility(View.GONE);
                        gridViewHolder.viewFunction.setVisibility(View.GONE);

                        // If the clicked item is a text file, download and edit function are visible
                    } else if (item.getItemType().equals("text-file")) {
                        gridViewHolder.viewFunction.setVisibility(View.VISIBLE);
                        gridViewHolder.downloadFunction.setVisibility(View.VISIBLE);

                        // If the clicked item is a normal file, download function is visible, edit function is gone
                    } else {
                        gridViewHolder.downloadFunction.setVisibility(View.VISIBLE);
                        gridViewHolder.viewFunction.setVisibility(View.GONE);
                    }

                    Log.i("Is Text File", item.getItemName() + " " + item.getItemType());
                    dialog.show();

                    // Set action for the cancel button in the dialog
                    // When button is clicked, the dialog disappears
                    gridViewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    // Set action for the cancel button in the dialog
                    // When button is clicked, the dialog checks for the checked radio button
                    // and determines which operation to perform
                    gridViewHolder.okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int selectedRadioButtonId = gridViewHolder.functionList.getCheckedRadioButtonId();

                            // Find which radioButton is checked by id
                            if (selectedRadioButtonId == gridViewHolder.viewFunction.getId())
                                // Open text file to view
                                mainActivity.activateOpenFunction(context, item.getItemPath(), item.getItemName());

                            else if (selectedRadioButtonId == gridViewHolder.downloadFunction.getId()) {
                                // Download file
                                DownloadFile downloadProcess = new DownloadFile(context,
                                        item.getItemName(), item.getItemPath());
                                downloadProcess.execute();

                            } else if (selectedRadioButtonId == gridViewHolder.renameFunction.getId())
                                // Rename file or folder
                                mainActivity.activateRenameFunction(context,
                                        item.getItemParentPath(), item.getItemName(), item.getItemType());

                            else if (selectedRadioButtonId == gridViewHolder.moveFunction.getId())
                                // Move file or folder. Open the file explorer dialog to choose the destination first.
                                mainActivity.showFileExplorerDialog(context, item.getItemPath(),
                                        item.getItemName(), "dropbox-filesystem");

                            else if (selectedRadioButtonId == gridViewHolder.deleteFunction.getId())
                                // Delete file or folder
                                mainActivity.activateDeleteFunction(context, item.getItemPath());
                            else
                                // Notify the user if he/she clicked OK without choosing anything
                                DataStorage.showNoti(context, "Please choose a function.");
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
        return convertView;
    }
}
