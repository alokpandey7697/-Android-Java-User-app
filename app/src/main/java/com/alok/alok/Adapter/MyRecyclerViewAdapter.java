package com.alok.alok.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alok.alok.Buyer.HomeActivity;
import com.alok.alok.Buyer.storeList;
import com.alok.alok.R;
import com.alok.alok.models.shopModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<shopModel> profiles;

    public MyRecyclerViewAdapter(Context c , ArrayList<shopModel> p)
    {
        context = c;
        profiles = p;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.store_list_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.category.setText(profiles.get(position).getCategory());
       // holder.lattitude.setText(profiles.get(position).getLattitude());
    //    holder.phone.setText(profiles.get(position).getPhone());
        holder.address.setText(profiles.get(position).getAddress());
       // holder.longitude.setText(profiles.get(position).getLongitude());
      //  holder.phone.setText(profiles.get(position).getPhone());
        holder.email.setText(profiles.get(position).getPhone());
        holder.name.setText(profiles.get(position).getName());


        Glide.with(holder.image).
                load(profiles.get(position).getImage()).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof storeList) {
                   Intent intent = new Intent(context, HomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("sid",profiles.get(position).getSid());
                  intent.putExtras(bundle);
                   context.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public interface ItemClickListener {
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,email,phone,address,longitude,lattitude,category;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            address = (TextView) itemView.findViewById(R.id.address);
            category = (TextView) itemView.findViewById(R.id.category);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
        public void onClick(final int position)
        {

        }
    }
}
