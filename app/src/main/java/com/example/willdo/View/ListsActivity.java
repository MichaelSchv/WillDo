package com.example.willdo.View;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.willdo.Adapters.ListAdapter;
import com.example.willdo.Data.FirestoreManager;
import com.example.willdo.Model.List;
import com.example.willdo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ListsActivity extends AppCompatActivity {

    private RecyclerView lists_RCV_lists;
    private FloatingActionButton lists_FAB_add;

    private ArrayList<List> lists;
    private ListAdapter listAdapter;
    private FirestoreManager firestoreManager;
    private FirebaseAuth firebaseAuth;
    private boolean initialFetchDone = false;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        findViews();
        initViews();
        firestoreManager = new FirestoreManager();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null && !initialFetchDone)
        {
            fetchLists(firebaseAuth.getCurrentUser().getUid());
            initialFetchDone = true;
        }

        lists_FAB_add.setOnClickListener(v -> showNewListDialog());
    }


    private void showNewListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = getLayoutInflater().inflate(R.layout.single_list_add_layout, null);
        builder.setTitle("Create New List");
        TextInputEditText listAdd_ET_title = popupView.findViewById(R.id.listAdd_ET_title);
        MaterialButton listAdd_BTN_create = popupView.findViewById(R.id.listAdd_BTN_create);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.show();

        listAdd_BTN_create.setOnClickListener(v->{
            String title = listAdd_ET_title.getText().toString().trim();
            if(!title.isEmpty())
            {
                String listId =firestoreManager.createNewList(title, this,firebaseAuth, new FirestoreManager.CreateListCallback() {
                    @Override
                    public void onListCreated(Context context, List list) {
                        String str = "List " + list.getTitle() + " created";
                        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                        updateListsDisplay(list);
                    }

                    @Override
                    public void onCreateFailed(Exception e) {
                        Toast.makeText(ListsActivity.this, "Unknown error, list not created", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
                Toast.makeText(this, "List name can't be empty", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(authStateListener!= null)
            firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(firebaseAuth.getCurrentUser() != null && !initialFetchDone)
        {
            fetchLists(firebaseAuth.getCurrentUser().getUid());
            initialFetchDone = true;
        }
    }

    private void fetchLists(String uid) {
        firestoreManager.fetchUserLists(uid, new FirestoreManager.FetchListsCallback() {
            @Override
            public void onListsFetched(java.util.List<List> lists) {
                ListsActivity.this.lists.clear();
                ListsActivity.this.lists.addAll(lists);
                listAdapter.notifyDataSetChanged();
                Log.d("DataCheck", "Fetched " + lists.size() + "lists");
                for(List list : lists){
                    Log.d("DataVerify", "Title: " + list.getTitle() + ", participants: " + list.getParticipants());
                    Log.d("DataCheck", list.toString());
                }
            }

            @Override
            public void onFetchFailed(Exception e) {
                Toast.makeText(ListsActivity.this, "Failed to fetch lists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        runOnUiThread(()->{
            lists = new ArrayList<>();
            listAdapter = new ListAdapter(this,lists);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            lists_RCV_lists.setLayoutManager(linearLayoutManager);
            lists_RCV_lists.setAdapter(listAdapter);
        });
    }

    private void findViews() {
        lists_RCV_lists = findViewById(R.id.lists_RCV_lists);
        lists_FAB_add = findViewById(R.id.lists_FAB_add);

    }
    private void updateListsDisplay(List newList) {
        //lists.add(newList);
        listAdapter.notifyDataSetChanged();
    }
}