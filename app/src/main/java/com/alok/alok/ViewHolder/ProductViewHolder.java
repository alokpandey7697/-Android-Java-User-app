package com.alok.alok.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alok.alok.Interfaces.ItemClicklistener;
import com.alok.alok.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName,txtProductDescription,txtProductPrice,txtProductCategory;
    public ImageView imageView;
    public ItemClicklistener listener;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=(ImageView) itemView.findViewById(R.id.product_image);
        txtProductDescription=(TextView)itemView.findViewById(R.id.product_Description);
        txtProductName=(TextView)itemView.findViewById(R.id.product_name);
        txtProductPrice=(TextView)itemView.findViewById(R.id.product_Price);
        txtProductCategory = (TextView)itemView.findViewById(R.id.product_category);
    }

    public void setItemClickListener(ItemClicklistener listener){
        this.listener=listener;
    }
    @Override
    public void onClick(View view) {
    listener.onClick(view,getAdapterPosition(),false);
    }
}
