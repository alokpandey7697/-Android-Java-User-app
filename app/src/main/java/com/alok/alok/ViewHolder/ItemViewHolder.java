package com.alok.alok.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alok.alok.Interfaces.ItemClicklistener;
import com.alok.alok.R;


public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName,txtProductDescription,txtProductPrice,txtProductStatus,txtProductCategory;
    public ImageView imageView;
    public ItemClicklistener listener;
    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=(ImageView) itemView.findViewById(R.id.product_seller_image);
        txtProductDescription=(TextView)itemView.findViewById(R.id.product_seller_Description);
        txtProductName=(TextView)itemView.findViewById(R.id.product_seller_name);
        txtProductPrice=(TextView)itemView.findViewById(R.id.product_seller_Price);
        txtProductStatus=(TextView)itemView.findViewById(R.id.product_seller_status);
        txtProductCategory=(TextView)itemView.findViewById(R.id.product_category_temp);
    }

    public void setItemClickListener(ItemClicklistener listener){
        this.listener=listener;
    }
    @Override
    public void onClick(View view) {
        listener.onClick(view,getAdapterPosition(),false);
    }
}