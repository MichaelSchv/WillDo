package com.example.willdo.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.willdo.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ItemViewHolder> {
    private Context context;
    private ArrayList<String> emails;

    public ParticipantsAdapter(Context context, ArrayList<String> emails){
        this.context = context;
        this.emails = emails;
    }

    @NonNull
    @Override
    public ParticipantsAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_participant_layout,parent,false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String currentEmail = emails.get(position);
        holder.participant_LBL_email.setText(currentEmail);
        Log.d("ParticipantsAdapter", "Binding email: " + currentEmail + " at position: " + position);
        //todo- long press to delete

    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        MaterialTextView participant_LBL_email;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private void findViews(View itemView) {
            participant_LBL_email = itemView.findViewById(R.id.participant_LBL_email);
        }
    }
}
