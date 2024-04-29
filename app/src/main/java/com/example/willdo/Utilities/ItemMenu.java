package com.example.willdo.Utilities;

import android.content.Context;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;

import com.example.willdo.Data.FirestoreManager;
import com.example.willdo.Model.Item;
import com.example.willdo.Model.List;
import com.example.willdo.R;
import com.example.willdo.View.ListEditActivity;

public class ItemMenu {
    private Item item;
    private FirestoreManager firestoreManager;

    public ItemMenu(Item item){
        this.item = item;
        firestoreManager = new FirestoreManager();
        firestoreManager.initFSM();
    }

    public void showItemContextPopup(Context context, View view, List currentList, ItemContextMenuListener listener) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.view_item_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getItemId() == R.id.contextmenu_BTN_edit){
                handleEdit(item, context, listener);
                popupMenu.dismiss();
                return true;
            }
            else if(menuItem.getItemId() == R.id.contextmenu_BTN_delete){
                handleDelete(item, context,listener, currentList);
            }
            return false;
        });
    }

    private void handleDelete(Item item, Context context, ItemContextMenuListener listener, List currentList) {
        currentList.removeItem(item);

        firestoreManager.updateList(currentList, new FirestoreManager.AddItemCallback() {
            @Override
            public void onSuccess() {
                listener.onDelete();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void handleEdit(Item item, Context context, ItemContextMenuListener listener) {
        ListEditActivity.launchItemEditActivity(context,item, true);
    }



    public interface ItemContextMenuListener {
        void onEdit();
        void onDelete();
    }
}
