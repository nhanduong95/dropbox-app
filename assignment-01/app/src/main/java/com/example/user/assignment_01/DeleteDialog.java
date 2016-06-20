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

import java.util.zip.Inflater;

@SuppressLint("ValidFragment")
public class DeleteDialog extends DialogFragment {
    private Context context;
    private String itemPath;

    private Button okButton;
    private Button cancelButton;

    @SuppressLint("ValidFragment")
    public DeleteDialog(Context context, String itemPath){
        this.context = context;
        this.itemPath = itemPath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.delete_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        okButton = (Button) view.findViewById(R.id.delete_ok);
        cancelButton = (Button) view.findViewById(R.id.delete_cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteItem deleteProcess = new DeleteItem(context, itemPath);
                deleteProcess.execute();
            }
        });

        return view;
    }
}
