package com.example.user.assignment_01;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 23-Nov-15.
 */
public class ViewAdapterForDialog extends ArrayAdapter<Item> {
    Context context;
    Item chosenDialogItem;
    private ArrayList<Item> list;
    private int resourceId;

    public ViewAdapterForDialog(Context context, int resourceId, ArrayList<Item> list){
        super(context, resourceId, list);

        this.context = context.getApplicationContext();
        this.list = list;
        this.resourceId = resourceId;
    }

    static class ViewHolder{
        TextView itemName;
        TextView itemIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;
        chosenDialogItem = getItem(position);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Inflate a new view hierarchy from the specified xml resource.
            // 1st arg: ID for an XML layout resource to load
            // 2nd arg: Optional parent view of the hierarchy
            convertView = inflater.inflate(R.layout.list_row_dialog, null);

            viewHolder = new ViewHolder();
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.dialog_item_name);
            viewHolder.itemIcon = (TextView) convertView.findViewById(R.id.dialog_item_icon);

            // Import FontAwesome and set the imported for the TextView
            Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.woff");
            viewHolder.itemIcon.setTypeface(fontAwesome);

            // Tag the ListViewHolder object with its relevant view
            convertView.setTag(viewHolder);
        }
        else
            // Get the ListViewHolder object for the convertView
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.itemName.setText(chosenDialogItem.getItemName());
        viewHolder.itemIcon.setText(chosenDialogItem.getIconId());
        return convertView;
    }
}