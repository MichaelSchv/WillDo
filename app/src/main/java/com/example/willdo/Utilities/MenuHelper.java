package com.example.willdo.Utilities;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.willdo.Adapters.ParticipantsAdapter;
import com.example.willdo.Data.FirestoreManager;
import com.example.willdo.Model.List;
import com.example.willdo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class MenuHelper {
    //private Context context;
    private List list;
    private FirestoreManager firestoreManager;

    public MenuHelper(List list) {
        this.list = list;
        firestoreManager = new FirestoreManager();
        firestoreManager.initFSM();
    }

    public void showPopupMenu(Context context, View view, MenuActionListener listener) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.list_view_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.listView_rename_list){
                showRenameDialog(list, context, listener);
                popupMenu.dismiss();
                return true;
            }
            else if(item.getItemId() == R.id.listView_participants){
                showParticipantsDialog(list, context);
                Log.d("participants", "participants");
                return true;
            }
            else if (item.getItemId() == R.id.listView_delete_list) {
                deleteList(list, context,listener);
                return true;
            }
        return false;
        });
    }

    private void showParticipantsDialog(List list, Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.participants_layout, null);

        RecyclerView participants_RCV_emails = dialogView.findViewById(R.id.participants_RCV_emails);
        FloatingActionButton participants_FAB_add = dialogView.findViewById(R.id.participants_FAB_add);
        ArrayList<String> emails = list.getParticipants();
        ParticipantsAdapter adapter = new ParticipantsAdapter(context, emails);
        participants_RCV_emails.setLayoutManager(new LinearLayoutManager(context));
        participants_RCV_emails.setAdapter(adapter);

        PopupWindow popupWindow = new PopupWindow(dialogView,WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true);

        participants_FAB_add.setOnClickListener(v->{

        });
        popupWindow.showAtLocation(dialogView, Gravity.CENTER, 0,0);
    }

    private void deleteList(List list, Context context, MenuActionListener listener) {
        firestoreManager.deleteList(list)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            listener.onDelete();
                    }
                });
    }
    private void showRenameDialog(List list, Context context, MenuActionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.single_list_rename_layout, null);
        TextInputEditText listRename_ET_title = view.findViewById(R.id.listRename_ET_title);
        listRename_ET_title.setText(list.getTitle());
        AlertDialog alertDialog = builder.setView(view).create();

        MaterialButton listRename_BTN_rename = view.findViewById(R.id.listRename_BTN_rename);
        listRename_BTN_rename.setOnClickListener(v-> {
            String newTitle= listRename_ET_title.getText().toString();
            if(!newTitle.isEmpty()){
                list.setTitle(newTitle);
                firestoreManager.updateList(list, new FirestoreManager.AddItemCallback() {
                    @Override
                    public void onSuccess() {
                        listener.onRename();
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
                alertDialog.dismiss();

            }
        });
        alertDialog.show();

    }
    public interface MenuActionListener {
        void onRename();
        void onViewParticipants();
        void onDelete();
    }
}
