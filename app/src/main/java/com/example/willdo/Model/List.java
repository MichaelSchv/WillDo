package com.example.willdo.Model;

import java.io.Serializable;
import java.util.ArrayList;


public class List implements Serializable {
    private String title;
    private ArrayList<Item> items;
    private ArrayList<String> participants;
    private int completedItemsCount;
    private int totalItemsCount;

    private String ID;

    public List()
    {
        this.title = "";
        this.participants = new ArrayList<>();
        this.items = new ArrayList<>();
        this.completedItemsCount = 0;
        this.totalItemsCount=0;
    }

    public List(String title, String ID)
    {
        this.title = title;
        this.ID = ID;
        this.items = new ArrayList<>();
        this.participants = new ArrayList<>();
        this.completedItemsCount =0;
        this.totalItemsCount=0;
    }
    public int getCompletedItemsCount(){
        return this.completedItemsCount;
    }
    public void setCompletedItemsCount(int completedItemsCount){
        this.completedItemsCount = completedItemsCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Item> getItems(){
        return this.items;
    }

    public void setItems(ArrayList<Item> items){
        this.items = items;
    }
    public int getTotalItemsCount() {
        return this.totalItemsCount;
    }

    public void setTotalItemsCount(int totalItemsCount) {
        this.totalItemsCount = totalItemsCount;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void addItem(Item item) {
        items.add(item);
        totalItemsCount++;
        if(item.isCompleted())
            completedItemsCount++;
    }

    public void removeItem(Item item) {
        items.remove(item);
        totalItemsCount--;
        if(item.isCompleted())
            completedItemsCount--;
    }

    public void addParticipant(String email){
        this.participants.add(email);
    }
    public ArrayList<String> getParticipants(){
        return this.participants;
    }

    @Override
    public String toString() {
        return "title='" + title + '\'' +
                ", items=" + items +
                ", participants=" + participants +
                ", completedItemsCount=" + completedItemsCount +
                ", ID='" + ID + '\'' +
                '}';
    }
}
