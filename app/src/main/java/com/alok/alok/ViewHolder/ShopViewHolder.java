package com.alok.alok.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alok.alok.Interfaces.ItemClicklistener;
import com.alok.alok.R;

public class ShopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView name,email,phone,address,longitude,lattitude,category;
    public ImageView image;
    public ItemClicklistener listener;

    public ShopViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        email = (TextView) itemView.findViewById(R.id.email);
        address = (TextView) itemView.findViewById(R.id.address);
        category = (TextView) itemView.findViewById(R.id.category);
        image = (ImageView) itemView.findViewById(R.id.image);
    }

    public void setItemClickListener(ItemClicklistener listener){
        this.listener=listener;
    }
    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }

}