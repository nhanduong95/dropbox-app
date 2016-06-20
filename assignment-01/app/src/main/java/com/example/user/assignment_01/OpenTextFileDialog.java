package com.example.user.assignment_01;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileWriter;

@SuppressLint("ValidFragment")
public class OpenTextFileDialog extends DialogFragment{
    private Context context;

    private String filePath;
    private String fileName;

    private Button okButton;
    private Button cancelButton;
    private TextView clickedFileName;
    static EditText textEditor;

    @SuppressLint("ValidFragment")
    public OpenTextFileDialog(Context context, String filePath, String fileName){
        this.context = context;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.view_text_file_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        okButton = (Button) view.findViewById(R.id.edit_ok);
        cancelButton = (Button) view.findViewById(R.id.edit_cancel);
        clickedFileName = (TextView) view.findViewById(R.id.file_name);
        textEditor = (EditText) view.findViewById(R.id.text_editor);

        clickedFileName.setText(fileName);
        textEditor.setMovementMethod(new ScrollingMovementMethod());

        ReadTextFile readFileProcess = new ReadTextFile(context, filePath);
        readFileProcess.execute();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveTextFile saveTextFileProcess = new SaveTextFile(context, filePath, textEditor.getText().toString());
                saveTextFileProcess.execute();
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
