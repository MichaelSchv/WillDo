package com.example.willdo.Model;

import java.io.Serializable;

public class Item implements Serializable {
    public static enum unitType {NONE,KG, G, L, ML}
    private String id;
    private String title;
    private String comment;
    private boolean completed;
    private long quantity;
    private unitType unit;
    private String imageURI;


    public Item() {
        this.title = "";
        this.completed = false;
        this.quantity=1L;
        this.unit = unitType.NONE;
        this.comment ="";
    }
    public Item(String title) {
        this.id = null;
        this.title = title;
        this.completed = false;
        this.quantity=1L;
        this.unit = unitType.NONE;
        this.comment ="";
    }

    public Item(String title, long quantity, Item.unitType unit, String comment, boolean completed){
        this.id = null;
        this.title = title;
        this.completed = completed;
        this.quantity = quantity;
        this.unit = unit;
        this.comment =comment;
    }

    public String getId() {
        return id;
    }

    public Item setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Item setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Item setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public Item setCompleted(boolean completed) {
        this.completed = completed;
        return this;
    }

    public long getQuantity() {
        return quantity;
    }

    public Item setQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public unitType getUnit() {
        return unit;
    }

    public Item setUnit(unitType unit) {
        this.unit = unit;
        return this;
    }

    public void setImageURI(String URI){
        this.imageURI = URI;
    }

    public String getImageURI() {
        return imageURI;
    }

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", isCompleted=" + completed +
                ", quantity=" + quantity +
                '}';
    }
}
