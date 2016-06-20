package com.example.user.assignment_01;

/*
 * Defines the property of a filesystem chosenDialogItem (e.g. a file or a directory)
 */
public class Item {
    private int iconId;
    private String itemName;
    private String itemParentPath;
    private String itemPath;
    private String type;

    public Item(String itemName, String itemPath){
        this.itemName = itemName;
        this.itemPath = itemPath;
    }

    public Item(String itemName, String itemPath, int iconId){
        this.itemName = itemName;
        this.itemPath = itemPath;
        this.iconId = iconId;
    }

    public String getItemName(){
        return itemName;
    }
    public void setItemName(String itemName){
        this.itemName = itemName;
    }

    public String getItemPath(){
        return itemPath;
    }
    public void setItemPath(String itemPath){
        this.itemPath = itemPath;
    }

    public int getIconId(){
        return iconId;
    }
    public void setIconId(int iconId){
        this.iconId = iconId;
    }

    public String getItemType(){
        return this.type;
    }
    public void setItemType(String type){
        this.type = type;
    }

    public String getItemParentPath(){
        return this.itemParentPath;
    }
    public void setItemParentPath(String itemParentPath){
        this.itemParentPath = itemParentPath;
    }
}
