package com.alok.alok.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alok.alok.Map.MapsActivity;
import com.alok.alok.R;
import com.alok.alok.ViewHolder.CartViewHolderAdmin;
import com.alok.alok.models.Cart;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminUserProductActivity extends AppCompatActivity {
    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference CartListRef,sellerAddRef;
    private String userID= "",deslat, deslon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_product);
        userID=getIntent().getStringExtra("uid");
        deslat=getIntent().getStringExtra("deslat");
        deslon=getIntent().getStringExtra("deslon");
        Log.d("chek",userID+ " " + deslat + " " + deslon);

        productsList=findViewById(R.id.products_list);
        productsList.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);
        CartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin view").child(userID).child("Products");
        //sellerAddRef=FirebaseDatabase.getInstance().getReference().child("Seller").child("Admin view").child(userID).child("Products")
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart>options=new FirebaseRecyclerOptions.Builder<Cart>().setQuery(CartListRef,Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolderAdmin>adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolderAdmin>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolderAdmin holder, int position, @NonNull Cart model) {
                holder.TxtProductQuantity.setText( "Quantity: "+model.getQuantity());
                holder.txtProductPrice.setText(model.getPrice());
                holder.txtProductName.setText(model.getPname());
                Log.d("chek",model.getPname());

                holder.TxtProductAddress.setText("Shop Address :"+model.getSellerAddress());
                Glide.with(holder.imageView).load(model.getImage()).into(holder.imageView);
               /* holder.showMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminUserProductActivity.this, MapsActivity.class);
//                        Log.d("value",deslat);
//                        Log.d("value",deslon);
//                        Log.d("value",model.getLattitude());
//                        Log.d("value",model.getLongitude());
                        intent.putExtra("deslat",deslat);
                        intent.putExtra("deslon",deslon);
                        intent.putExtra("orlat",model.getLattitude());
                        intent.putExtra("orlon",model.getLongitude());
                        startActivity(intent);
                    }
                });*/
            }

            @NonNull
            @Override
            public CartViewHolderAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout_admin,parent,false);
                CartViewHolderAdmin holder = new CartViewHolderAdmin(view);
                return holder;
            }
        };
        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}
