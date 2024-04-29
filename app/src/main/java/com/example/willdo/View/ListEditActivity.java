package com.example.willdo.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.willdo.Adapters.ItemAdapter;
import com.example.willdo.Data.FirestoreManager;
import com.example.willdo.Interface.ItemStatusChangeListener;
import com.example.willdo.Logic.ItemListManager;
import com.example.willdo.Model.Item;
import com.example.willdo.Model.List;
import com.example.willdo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;



public class ListEditActivity extends AppCompatActivity {


    private MaterialTextView listEdit_LBL_title;
    private ImageButton listEdit_IMG_menu;
    private RecyclerView listEdit_RCV_items;
    private FloatingActionButton listEdit_BTN_addItem;
    private List currentList;
    private ItemListManager itemListManager;
    private ItemAdapter itemAdapter;
    FirestoreManager firestoreManager;

    private ItemStatusChangeListener itemStatusChangeListener;
    private static ActivityResultLauncher<Intent> itemResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_edit_layout);
        findViews();
        firestoreManager = new FirestoreManager();
        firestoreManager.initFSM();
        currentList = (List) getIntent().getSerializableExtra("myList");
        if(currentList != null)
        {
            listEdit_LBL_title.setText(currentList.getTitle());
            initViews();
            itemListManager.sortList(currentList.getItems());
        }
        //handleMenu();
        itemResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result->{
                if(result.getResultCode() == RESULT_OK){
                    Item newItem = (Item) result.getData().getSerializableExtra("newItem");
                    if(newItem!= null){
                        updateItemInDB(newItem, true);
                    }
                }
                else if(result.getResultCode() == RESULT_FIRST_USER){
                    Item updatedItem = (Item) result.getData().getSerializableExtra("updatedItem");
                    if(updatedItem!= null){
                        updateItemInDB(updatedItem, false);
                    }
                }
            }
        );
        listEdit_BTN_addItem.setOnClickListener(v->showAddItemPopup());

    }

    private void showAddItemPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = getLayoutInflater().inflate(R.layout.single_item_add_layout, null);
        TextInputEditText itemAdd_ET_title = popupView.findViewById(R.id.itemAdd_ET_title);
        MaterialButton itemAdd_BTN_save = popupView.findViewById(R.id.itemAdd_BTN_save);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.show();

        itemAdd_BTN_save.setOnClickListener(v->{
            String title = itemAdd_ET_title.getText().toString().trim();
            if(!title.isEmpty()){
                int status = handleItemAddition(title);
                switch (status){
                    case ItemListManager.ITEM_NOT_FOUND:
                        Toast.makeText(this,"Item added",Toast.LENGTH_SHORT).show();
                        break;
                    case ItemListManager.ITEM_FOUND_COMPLETED:
                        Toast.makeText(this,"Item found and is now active",Toast.LENGTH_SHORT).show();
                        break;
                    case ItemListManager.ITEM_FOUND_ACTIVE:
                        Toast.makeText(this,"Item found, incremented quantity by 1",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(this,"Unexpected error",Toast.LENGTH_SHORT).show();

                }
                sortAndRefresh();
            }
            else
                Toast.makeText(this, "No items added", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

    }

    private int handleItemAddition(String title) {
        Item item;
        int status = itemListManager.checkItemStatus(title, currentList.getItems());
        switch(status){
            case ItemListManager.ITEM_NOT_FOUND:
                Log.d("ListEditActivity", "handleItemAddition new item for " + title);
                launchItemEditActivity(ListEditActivity.this,title,false);
                break;
            case ItemListManager.ITEM_FOUND_COMPLETED:
                Log.d("ListEditActivity", "handleItemAddition reactivate for " + title);
                itemListManager.reactivateItem(currentList.getItems(), title);
                item = itemListManager.getItemByTitle(title, currentList.getItems());
                updateItemInDB(item, false);
                break;
            case ItemListManager.ITEM_FOUND_ACTIVE:
                Log.d("ListEditActivity", "handleItemAddition increment for " + title);
                itemListManager.incrementItemQuantity(currentList.getItems(), title);
                item = itemListManager.getItemByTitle(title, currentList.getItems());
                updateItemInDB(item, false);
                break;
            default:
        }
        sortAndRefresh();
        return status;
    }

    private void updateItemInDB(Item item, boolean isNewItem) {
        if(isNewItem)
            currentList.addItem(item);
        else
        {
            itemListManager.updateExistingItem(currentList.getItems(), item);
        }
        firestoreManager.updateList(currentList, new FirestoreManager.AddItemCallback() {
            @Override
            public void onSuccess() {
                sortAndRefresh();
                runOnUiThread(() ->Toast.makeText(ListEditActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(ListEditActivity.this, "Failed to update item: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    public static void launchItemEditActivity(Context context, Object object, boolean isEdit) {
        Intent intent = new Intent(context, ItemEditActivity.class);
        if(!isEdit){
            String title = (String) object;
            intent.putExtra("title", title);
        }
        else{
            Item item = (Item) object;
            intent.putExtra("item", item);
        }
        itemResultLauncher.launch(intent);

    }
    public void sortAndRefresh() {
        itemListManager.sortList(currentList.getItems()); // todo: update adapter to show changes
        if (itemAdapter != null) {
            itemAdapter.updateItems(currentList.getItems());
            itemAdapter.notifyDataSetChanged();
        }
        else {
            itemAdapter = new ItemAdapter(this, currentList.getItems(), currentList);
            itemAdapter.setItemStatusChangeListener(itemStatusChangeListener);
            listEdit_RCV_items.setAdapter(itemAdapter);
        }
        forceUpdateRecyclerView();
    }

    private void findViews() {
        listEdit_LBL_title = findViewById(R.id.listEdit_LBL_title);
        listEdit_IMG_menu = findViewById(R.id.listEdit_IMG_menu);
        listEdit_RCV_items = findViewById(R.id.listEdit_RCV_items);
        listEdit_BTN_addItem = findViewById(R.id.listEdit_BTN_addItem);

    }
    private void initViews() {
        itemListManager = new ItemListManager();
        itemAdapter = new ItemAdapter(this,currentList.getItems(), currentList);
        itemStatusChangeListener = new ItemStatusChangeListener() {
            @Override
            public void onItemCompletedChanged(Item item, int position) {
                int[] indexChanges = itemListManager.sortAndGetNewIndex(currentList.getItems(), item);

                if(item.isCompleted())
                    currentList.setCompletedItemsCount(currentList.getCompletedItemsCount()+1);
                else
                    currentList.setCompletedItemsCount(currentList.getCompletedItemsCount()-1);

                if(indexChanges[0] != indexChanges[1])
                    itemAdapter.notifyItemMoved(indexChanges[0],indexChanges[1]);

                itemAdapter.notifyDataSetChanged();
                updateItemInDB(item,false);

            }
        };
        itemAdapter.setItemStatusChangeListener(itemStatusChangeListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        listEdit_RCV_items.setLayoutManager(linearLayoutManager);
        listEdit_RCV_items.setAdapter(itemAdapter);
    }
    private void forceUpdateRecyclerView() {
        listEdit_RCV_items.invalidate();
        itemAdapter.notifyDataSetChanged();
    }
}