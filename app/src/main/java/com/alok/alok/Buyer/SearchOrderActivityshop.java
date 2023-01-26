package com.alok.alok.Buyer;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alok.alok.R;
import com.alok.alok.ViewHolder.ShopViewHolder;
import com.alok.alok.models.shopModel;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SearchOrderActivityshop extends AppCompatActivity {

    private Button SearchBtn;
    private EditText inputtxt;
    private String SearchInput;
    private RecyclerView searchList;

    public SearchOrderActivityshop() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_search_order_shop);
        inputtxt=findViewById(R.id.search_product_name);
        SearchBtn=findViewById(R.id.searchBtn);
        searchList=findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchOrderActivityshop.this));

        SearchBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SearchInput=inputtxt.getText().toString();
                onStart();
            }
        });
    }
    protected void onStart()
    {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Seller");
        FirebaseRecyclerOptions<shopModel>options=new FirebaseRecyclerOptions.Builder<shopModel>()
                .setQuery(reference.orderByChild("name").startAt(SearchInput),shopModel.class).build();
        FirebaseRecyclerAdapter<shopModel, ShopViewHolder>adapter=new FirebaseRecyclerAdapter<shopModel, ShopViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ShopViewHolder holder, int position, @NonNull shopModel profiles) {

                holder.category.setText(profiles.getCategory());
                // holder.lattitude.setText(profiles.get(position).getLattitude());
                //    holder.phone.setText(profiles.get(position).getPhone());
                holder.address.setText(profiles.getAddress());
                // holder.longitude.setText(profiles.get(position).getLongitude());
                //  holder.phone.setText(profiles.get(position).getPhone());
                holder.email.setText(profiles.getPhone());
                holder.name.setText(profiles.getName());

                Glide.with(holder.image).load(profiles.getImage()).into(holder.image);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            Intent intent = new Intent(SearchOrderActivityshop.this, HomeActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("sid",profiles.getSid());
                            intent.putExtras(bundle);
                            startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_list_row, parent, false);
                ShopViewHolder holder = new ShopViewHolder(view);
                return holder;
            }
        };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}
