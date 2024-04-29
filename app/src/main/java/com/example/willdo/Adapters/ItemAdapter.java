package com.example.willdo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.willdo.Interface.ItemStatusChangeListener;
import com.example.willdo.Model.Item;
import com.example.willdo.Model.List;
import com.example.willdo.R;
import com.example.willdo.Utilities.ImageLoader;
import com.example.willdo.Utilities.ItemMenu;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context context;
    private List currentList;
    private ArrayList<Item> itemList;
    private ItemStatusChangeListener itemStatusChangeListener;
    private ImageLoader imageLoader;

    public ItemAdapter(Context context, ArrayList<Item> itemList, List currentList) {
        this.context = context;
        this.itemList = itemList;
        this.imageLoader = new ImageLoader(context);
        this.currentList = currentList;
    }

    public void setItemStatusChangeListener(ItemStatusChangeListener listener){
        this.itemStatusChangeListener = listener;
    }


    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_view_layout,parent,false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item currentItem = itemList.get(position);
        holder.itemView_CHCKBX_completed.setOnCheckedChangeListener(null); // 1
        holder.itemView_CHCKBX_completed.setChecked(currentItem.isCompleted());
        if (currentItem.isCompleted()) {
            holder.itemView.setBackgroundColor(context.getColor(R.color.red_completed)); // Assuming 'reddishColor' is defined in your colors.xml
        } else {
            holder.itemView.setBackgroundColor(context.getColor(R.color.green_active)); // Default background color
        }
        holder.itemView_LBL_title.setText(currentItem.getTitle());

        holder.itemView_CHCKBX_completed.setOnCheckedChangeListener((buttonView, isChecked) -> {
                currentItem.setCompleted(isChecked);
                if(itemStatusChangeListener != null)
                    itemStatusChangeListener.onItemCompletedChanged(currentItem,position);
        });

        if(currentItem.getQuantity()==1 && currentItem.getUnit() == Item.unitType.NONE)
            holder.itemView_LLC_quantityAndUnits.setVisibility(View.GONE);
        else{
            holder.itemView_LLC_quantityAndUnits.setVisibility(View.VISIBLE);
            holder.itemView_LBL_quantity.setText(String.valueOf(currentItem.getQuantity()));
            if(currentItem.getUnit() == Item.unitType.NONE)
                holder.itemView_LBL_units.setVisibility(View.INVISIBLE);
            else
                holder.itemView_LBL_units.setText((currentItem.getUnit().toString()).toUpperCase());
        }


        if(currentItem.getComment() != null && !currentItem.getComment().equals("") && !currentItem.getComment().isEmpty()){
            holder.itemView_LBL_comment.setVisibility(View.VISIBLE);
            holder.itemView_LBL_comment.setText(currentItem.getComment());
        }
        else
            holder.itemView_LBL_comment.setVisibility(View.GONE);

        if(currentItem.getImageURI() != null && !currentItem.getImageURI().isEmpty()){
            imageLoader.load(currentItem.getImageURI(), holder.itemView_IMG_itemImage);
        }
        holder.itemView_IMG_itemImage.setOnClickListener(v -> showImagePopup(currentItem.getImageURI()));

        holder.itemView.setOnLongClickListener(v->{
            ItemMenu itemMenu = new ItemMenu(currentItem);
            itemMenu.showItemContextPopup(holder.itemView_CHCKBX_completed.getContext(), v, currentList, new ItemMenu.ItemContextMenuListener() {
                @Override
                public void onEdit() {
                    notifyDataSetChanged();
                }

                @Override
                public void onDelete() {
                   /* itemList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), itemList.size());*/
                    notifyDataSetChanged();
                }
            });
            return false;
        });
    }

    private void showImagePopup(String imageURI) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.image_popup, null);
        ShapeableImageView popup_IMG_enlargeImage = view.findViewById(R.id.popup_IMG_enlargeImage);
        ImageLoader imageLoader = new ImageLoader(context);
        imageLoader.load(imageURI,popup_IMG_enlargeImage);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateItems(ArrayList<Item> items){
        this.itemList = items;
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private CheckBox itemView_CHCKBX_completed;
        private MaterialTextView itemView_LBL_title;
        private MaterialTextView itemView_LBL_quantity;
        private MaterialTextView itemView_LBL_units;
        private MaterialTextView itemView_LBL_comment;
        private ShapeableImageView itemView_IMG_itemImage;
        private LinearLayoutCompat itemView_LLC_quantityAndUnits;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private void findViews(View itemView){
            itemView_CHCKBX_completed = itemView.findViewById(R.id.itemView_CHCKBX_completed);
            itemView_LBL_title = itemView.findViewById(R.id.itemView_LBL_title);
            itemView_LBL_quantity = itemView.findViewById(R.id.itemView_LBL_quantity);
            itemView_LBL_units = itemView.findViewById(R.id.itemView_LBL_units);
            itemView_LBL_comment = itemView.findViewById(R.id.itemView_LBL_comment);
            itemView_IMG_itemImage = itemView.findViewById(R.id.itemView_IMG_itemImage);
            itemView_LLC_quantityAndUnits = itemView.findViewById(R.id.itemView_LLC_quantityAndUnits);
        }

    }
}