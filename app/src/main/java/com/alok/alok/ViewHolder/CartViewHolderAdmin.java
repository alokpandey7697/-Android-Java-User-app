package com.alok.alok.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alok.alok.Interfaces.ItemClicklistener;
import com.alok.alok.R;

public class CartViewHolderAdmin extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName,  txtProductPrice, TxtProductQuantity, TxtProductAddress;
    public Button showMap;
    public ImageView imageView;
    private ItemClicklistener itemClicklistener;

    public CartViewHolderAdmin(View itemView)
    {
        super(itemView);
        txtProductName=itemView.findViewById(R.id.cart_product_name);
        txtProductPrice=itemView.findViewById(R.id.cart_product_price);
        TxtProductQuantity=itemView.findViewById(R.id.cart_product_quantity);
        TxtProductAddress=itemView.findViewById(R.id.sellerAddress);
        imageView=(ImageView)itemView.findViewById(R.id.adminImage);

    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        itemClicklistener.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClicklistener(ItemClicklistener itemClicklistener) {
        this.itemClicklistener = itemClicklistener;
    }
}
