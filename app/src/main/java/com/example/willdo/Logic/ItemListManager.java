package com.example.willdo.Logic;

import com.example.willdo.Model.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ItemListManager implements Comparator<Item> {
    public static final int ITEM_NOT_FOUND = -10;
    public static final int ITEM_FOUND_COMPLETED = 0;
    public static final int ITEM_FOUND_ACTIVE = 10;

    public void sortList(ArrayList<Item> items) {

        ArrayList<Item> completedItems = new ArrayList<>();
        ArrayList<Item> activeItems = new ArrayList<>();

        for (Item item : items) {
            if (item.isCompleted())
                completedItems.add(item);
            else
                activeItems.add(item);
        }

        Collections.sort(completedItems, Comparator.comparing(Item::getTitle));
        Collections.sort(activeItems, Comparator.comparing(Item::getTitle));

        items.clear();
        items.addAll(activeItems);
        items.addAll(completedItems);
    }

    public int[] sortAndGetNewIndex(ArrayList<Item> items, Item changedItem) {
        int oldIndex = items.indexOf(changedItem);
        sortList(items);
        int newIndex = items.indexOf(changedItem);
        return new int[]{oldIndex, newIndex};
    }

    public int checkItemStatus(String title, ArrayList<Item> items) {
        for (Item item : items) {
            if (title.equalsIgnoreCase(item.getTitle())) {
                if (item.isCompleted())
                    return ITEM_FOUND_COMPLETED;
                return ITEM_FOUND_ACTIVE;
            }
        }
        return ITEM_NOT_FOUND;
    }

    public int getIndexByTitle(String title, ArrayList<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            if (title.equalsIgnoreCase(items.get(i).getTitle()))
                return i;
        }
        return ITEM_NOT_FOUND;
    }

    public Item getItemByTitle(String title, ArrayList<Item> items) {
        return items.get(getIndexByTitle(title,items));
    }

    @Override
    public int compare(Item item1, Item item2) {
        if (!item1.isCompleted() && item2.isCompleted())
            return -1;
        else if (item1.isCompleted() && !item2.isCompleted())
            return 1;
        return item1.getTitle().compareToIgnoreCase(item2.getTitle());
    }

    public void reactivateItem(ArrayList<Item> items, String title) {
        int index = getIndexByTitle(title, items);
        if (index != ITEM_NOT_FOUND) {
            items.get(index).setCompleted(false);
        }
    }

    public void incrementItemQuantity(ArrayList<Item> items, String title) {
        int index = getIndexByTitle(title, items);
        if (index != ITEM_NOT_FOUND)
            items.get(index).setQuantity(items.get(index).getQuantity() + 1);
    }
    public void updateExistingItem(ArrayList<Item> items, Item item)
    {
        int index = getIndexByTitle(item.getTitle(), items);
        Item listItem = items.get(index);
        listItem.setQuantity(item.getQuantity());
        listItem.setCompleted(item.isCompleted());
        listItem.setComment(item.getComment());
        listItem.setUnit(item.getUnit());
        listItem.setImageURI(item.getImageURI());
    }
}
