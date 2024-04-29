package com.example.willdo.Interface;

import com.example.willdo.Model.Item;

public interface ItemStatusChangeListener {
    void onItemCompletedChanged(Item item, int position);
}
