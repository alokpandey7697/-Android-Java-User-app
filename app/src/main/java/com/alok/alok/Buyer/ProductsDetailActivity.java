package com.alok.alok.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alok.alok.models.Users;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.alok.alok.Prevalent.Prevalent;
import com.alok.alok.R;
import com.alok.alok.models.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductsDetailActivity extends AppCompatActivity {

    private Button addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberbutton;
    private TextView productprice,productname,productdescription;
    private String productID="",state="Normal";
    private String sellerAddress,downloadimgeurl,sid,lattitude,longitude;
     ImageView addIcon;
     ImageView removeIcon;
     TextView foodItemCount;
private boolean canOrder = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_products_detail);
        productID=getIntent().getStringExtra("pid");
        addToCartBtn=(Button)findViewById(R.id.pd_add_to_cart);
        //numberbutton=(ElegantNumberButton)findViewById(R.id.numberBtn);
        productImage=(ImageView)findViewById(R.id.product_image_details);
        productprice=(TextView)findViewById(R.id.product_price_details);
        productname=(TextView)findViewById(R.id.product_name_details);
        productdescription=(TextView)findViewById(R.id.product_description_details);
       addIcon= (ImageView)findViewById(R.id.food_details_food_add);
      removeIcon = (ImageView)findViewById(R.id.food_details_food_remove);
       foodItemCount  = (TextView)findViewById(R.id.food_details_item_count);

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currCount = Integer.parseInt(foodItemCount.getText().toString());
                if(currCount!=10){
                    currCount++;
                    foodItemCount.setText(String.valueOf(currCount));
                } else {
                    Toast.makeText(ProductsDetailActivity.this, "Maximum Limit Reached", Toast.LENGTH_SHORT).show();
                }
            }
        });

        removeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currCount = Integer.parseInt(foodItemCount.getText().toString());
                if(currCount!=1){
                    currCount--;
                    foodItemCount.setText(String.valueOf(currCount));
                } else {
                    Toast.makeText(ProductsDetailActivity.this, "Minimum Limit Reached ", Toast.LENGTH_SHORT).show();
                }
            }
        });


        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                if(state.equals("Order Placed") || state.equals("Order Shipped"))
//                {
//                    Toast.makeText(ProductsDetailActivity.this, "You can purchasee more products once your order is shipped or confirmed", Toast.LENGTH_LONG).show();
//                }
//                else
//                {
                if(canOrder)
                    addingTOCartList();
                else
                {
                    checkConnection();
                }
                //}
            }
        });

        getProductDetails(productID);

    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
    }

    private void addingTOCartList() {
        String savecurrentDate,saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        savecurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calForDate.getTime());

        DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object>cartMap=new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productname.getText().toString());
        cartMap.put("price",productprice.getText().toString());
        cartMap.put("date",savecurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",foodItemCount.getText().toString());
        cartMap.put("discount","");
        cartMap.put("lattitude",lattitude);
        cartMap.put("longitude",longitude);
        cartMap.put("sellerAddress",sellerAddress);
        cartMap.put("image",downloadimgeurl);
        cartMap.put("sid",sid);
        cartMap.put("uid", Prevalent.currentOnlineUsers.getUid());
        cartListRef.child("User view").child(Prevalent.currentOnlineUsers.getPhone()).child("Products").child(productID)
                .updateChildren(cartMap)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {



                    updateSid();


                    Toast.makeText(ProductsDetailActivity.this,"Added to cart list",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });




    }

    private void updateSid() {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.child("sidRef").child(sid).exists()){
                    HashMap<String,Object>userdataMap=new HashMap<>();
                    userdataMap.put("sid",sid);

                    RootRef.child("sidRef").child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                                           }
                                    else{
                                        checkConnection();
                                    }
                                }
                            });
                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
checkConnection();
            }
        });
    }
    public void checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            new AlertDialog.Builder(this)
                    .setMessage("Connection Error")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent intent = new Intent(ProductsDetailActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }
    private void getProductDetails(String productID) {
        DatabaseReference productreference = FirebaseDatabase.getInstance().getReference().child("products");
        productreference.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Products products=dataSnapshot.getValue(Products.class);
                productname.setText(products.getPname());
                productprice.setText(products.getPrice());
                productdescription.setText(products.getDescription());
                sellerAddress = products.getSellerAddress();
                lattitude = products.getLattitude();
                longitude = products.getLongitude();
                downloadimgeurl= products.getImage();
                sid=products.getSid();
                Glide.with(productImage).load(products.getImage()).into(productImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
checkConnection();
            }
        });
    }


    public void getUserData() {

        final DatabaseReference RootRef;
        final Users[] usersdata = new Users[1];
        String sid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Users").child(sid).exists()){
                    Prevalent.currentOnlineUsers=dataSnapshot.child("Users").child(sid).getValue(Users.class);
                }
                else{
                    Toast.makeText(ProductsDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void CheckOrderState() {

        DatabaseReference sidRef;
        sidRef = FirebaseDatabase.getInstance().getReference().child("sidRef").child(Prevalent.currentOnlineUsers.getPhone());


        sidRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0)
                {
            canOrder = true;
                }
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Log.d("phone",Prevalent.currentOnlineUsers.getPhone());
                    Log.d("sid",sid);
                    Log.d("sidd", (String) data.getValue());

                    if (!sid.equals(  (String) data.getValue())) {
                        CharSequence options[] = new CharSequence[]
                                {
                                       "OK", "remove item"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProductsDetailActivity.this);
                        builder.setTitle("You can't order from multiple seller at same time !");
                        builder.setCancelable(false);
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0)
                                {
                                  finish();
                                }
                                if(which == 1)
                                {
                                    Intent intent = new Intent(ProductsDetailActivity.this, CartActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    } else {
                        Log.d("restrict", "can place order");
                        canOrder = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
checkConnection();
            }
        });
    }
}
