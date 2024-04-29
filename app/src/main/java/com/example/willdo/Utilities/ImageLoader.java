package com.example.willdo.Utilities;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.willdo.R;


public class ImageLoader {
    private static Context appContext;

    public ImageLoader(Context context) {
        if (context == null)
            throw new IllegalArgumentException("Context cannot be null");
        appContext = context;
    }

    public void load (String link, ImageView imageView){
        Glide.
                with(appContext)
                .load(link)
                .placeholder(R.drawable.landscape_placeholder)
                .into(imageView);
    }

    public void load (int drawableId, ImageView imageView){
        Glide.
                with(appContext)
                .load(drawableId)
                .placeholder(R.drawable.landscape_placeholder)
                .into(imageView);
    }
}
