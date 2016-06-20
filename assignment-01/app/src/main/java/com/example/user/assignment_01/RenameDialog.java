package com.example.user.assignment_01;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by User on 26-Nov-15.
 */
@SuppressLint("ValidFragment")
public class RenameDialog extends DialogFragment {
    private Context context;

    private Button okButton;
    private Button cancelButton;
    private EditText itemNameEditor;

    private String fileExtension;
    private String clickedItemName;
    private String clickedItemParentPath;
    private String itemType;

    @SuppressLint("ValidFragment")
    public RenameDialog(Context context, String clickedItemParentPath,
                        String clickedItemName, String itemType){
        this.context = context;
        this.clickedItemParentPath = clickedItemParentPath;
        this.clickedItemName = clickedItemName;
        this.itemType = itemType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.rename_dialog, container, false);

        okButton = (Button) view.findViewById(R.id.rename_ok);
        cancelButton = (Button) view.findViewById(R.id.rename_cancel);
        itemNameEditor = (EditText) view.findViewById(R.id.item_name_editor);

        // If the clicked item is a file, check if it has extension
        if(itemType.equals("file") && clickedItemName.contains(".")){
            // If the clicked item has file extension,
            // allow user to edit the base name only.
            // The file extension needs to be intact (no modification is allowed)
            itemNameEditor.setText(clickedItemName.substring(0,
                    clickedItemName.lastIndexOf(".")));
            fileExtension = clickedItemName.substring(clickedItemName.lastIndexOf("."));
        }else
            // If the clicked item is a folder or is a file without any extension,
            // no need to worry about the extension
            itemNameEditor.setText(clickedItemName);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the clicked item is a file, check if it has extension
                if(itemNameEditor.getText().toString().equals(""))
                    DataStorage.showNoti(context, "Please enter a new name.");
                else{
                    if(itemType.equals("file") && clickedItemName.contains(".")){
                        // If the clicked item has file extension,
                        // allow user to edit the base name only.
                        // The file extension needs to be intact (no modification is allowed)
                        RenameItem renameProcess = new RenameItem(context, clickedItemParentPath,
                                clickedItemName, itemNameEditor.getText().toString() + fileExtension);
                        renameProcess.execute();
                    }else {
                        // If the clicked item is a folder or is a file without any extension,
                        // no need to worry about the extension
                        RenameItem renameProcess = new RenameItem(context, clickedItemParentPath,
                                clickedItemName, itemNameEditor.getText().toString());
                        renameProcess.execute();
                    }
                }
            }
        });
        return view;
    }
}
