package com.example.willdo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.willdo.Model.List;
import com.example.willdo.R;
import com.example.willdo.Utilities.MenuHelper;
import com.example.willdo.View.ListEditActivity;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    private Context context;
    private ArrayList<List> lists;
    public ListAdapter(Context context, ArrayList<List> lists){
        this.context = context;
        this.lists=lists;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_view_layout, parent, false);
        return new ListViewHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        List list = lists.get(position);
        holder.listView_LBL_title.setText(list.getTitle());
        String progress = list.getCompletedItemsCount() + " / " + list.getTotalItemsCount();
        holder.listView_LBL_progress.setText(progress);
        holder.listView_LBL_title.setOnClickListener(v -> {
            Intent intent = new Intent(context, ListEditActivity.class);
            intent.putExtra("myList", list);
            context.startActivity(intent);
        });
        holder.listView_IMG_menu.setOnClickListener(v -> {
            MenuHelper menu = new MenuHelper(list);
            menu.showPopupMenu(holder.listView_LBL_title.getContext(), v, new MenuHelper.MenuActionListener() {
                @Override
                public void onRename() {
                    notifyDataSetChanged();
                }

                @Override
                public void onViewParticipants() {

                }

                @Override
                public void onDelete() {
                    notifyDataSetChanged();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder{
        public MaterialTextView listView_LBL_title;
        public MaterialTextView listView_LBL_progress;
        public ImageButton listView_IMG_menu;

        public ListViewHolder(View view){
            super(view);
            findViews(view);
        }

        private void findViews(View view) {
            listView_LBL_title = view.findViewById(R.id.listView_LBL_title);
            listView_LBL_progress = view.findViewById(R.id.listView_LBL_progress);
            listView_IMG_menu = view.findViewById(R.id.listView_IMG_menu);
        }
    }
}
