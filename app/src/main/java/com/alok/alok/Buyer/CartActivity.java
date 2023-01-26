package com.alok.alok.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alok.alok.Map.MapActivity;
import com.alok.alok.Prevalent.Prevalent;
import com.alok.alok.R;
import com.alok.alok.ViewHolder.CartViewHolder;
import com.alok.alok.models.Cart;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private RecyclerView.LayoutManager layoutManager;
    private Button Processbtn;
    private TextView txttotal,txtmsg1;
    private int overallAmount=0;
    private String add;
    private String sid;
    public static int count_item= -1;
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
                            Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkConnection();
        recyclerview = findViewById(R.id.cart_list);
        recyclerview.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
        Processbtn=(Button)findViewById(R.id.process_btn);
        txttotal=(TextView)findViewById(R.id.total_price);
        txtmsg1=(TextView)findViewById(R.id.msg1);
       NoItemCart();
        Processbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double longitude = Double.parseDouble(Prevalent.currentOnlineUsers.getDelLongitude());
                double lattitude =Double.parseDouble(Prevalent.currentOnlineUsers.getDelLattitude());
                String address = Prevalent.currentOnlineUsers.getDelAddress();
                if (address != null && longitude != 1234 && lattitude != 1234 && !address.equals("ADDRESS") && !address.equals("")) {
                    CharSequence[] options = new CharSequence[]
                            {
                                    "CONFIRM", "CHANGE"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    TextView textView = new TextView(getApplicationContext());
                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView.setTextSize(20);
                    textView.setText("Shipping Address \n "+ address);
                    builder.setCustomTitle(textView);
                    builder.setCancelable(false);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==0)
                            {
                                add = address;
                                Intent intent =new Intent(CartActivity.this, ConferFinalOrderActivity.class);
                                // Log.d("jdj",data.getStringExtra("lattitude"));
                                // Log.d("jdj",data.getStringExtra("longitude"));
                                // intent.putExtra("sid",sid);
                                intent.putExtra("deslat",lattitude);
                                intent.putExtra("deslon",longitude);
                                intent.putExtra("TotalPrice",overallAmount);
                                intent.putExtra("Address",add );
                                intent.putExtra("sid",sid);
                                startActivity(intent);
                                finish();
                            }
                            if(which == 1)
                            {
                                txttotal.setText("Total Price= $" + String.valueOf(overallAmount));
                                Intent i = new Intent(CartActivity.this, MapActivity.class);
                                i.putExtra("TotalPrice", overallAmount);
                                Log.d("totalCartSEnd", String.valueOf(overallAmount));
                                startActivityForResult(i, 1);
                            }
                        }
                    });
                    builder.show();
Toast.makeText(getApplicationContext(), "No pre add", Toast.LENGTH_SHORT).show();
                } else {
                    txttotal.setText("Total Price= $" + String.valueOf(overallAmount));
                    Intent i = new Intent(CartActivity.this, MapActivity.class);
                    i.putExtra("TotalPrice", overallAmount);
                    Log.d("totalCartSEnd", String.valueOf(overallAmount));
                    startActivityForResult(i, 1);
                }
            }
        });
    }
    protected void onStart() {
        overallAmount = 0;
       // CheckOrderState();
        super.onStart();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart>options=
                new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef.child("User view").
                        child(Prevalent.currentOnlineUsers.getPhone()).child("Products"),Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder>adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {

                count_item++;
                Log.d("count", String.valueOf(count_item) + "   " + String.valueOf(position));

                if(count_item == 0)
    NoItemCart();

               /* "Quantity = "+
               * "Price "+
               * */

                holder.TxtProductQuantity.setText( model.getQuantity());
                holder.txtProductPrice.setText("₹" + model.getPrice());
                holder.txtProductName.setText(model.getPname());
                Glide.with(holder.product).load(model.getImage()).into(holder.product);

                sid = model.getSid();
                int oneTypeProductPrice=((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                holder.singleItemTotalCost.setText("₹" + oneTypeProductPrice  );
                overallAmount = overallAmount+oneTypeProductPrice;
txttotal.setText("Total Price= ₹" + String.valueOf(overallAmount));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]
                                {
                                       "Edit",
                                       "Remove"
                                }  ;
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart OPtions:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0)
                                {
                                    Intent intent = new  Intent(CartActivity.this, ProductsDetailActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                if(which==1)
                                {
                                    cartListRef.child("User view")
                                            .child(Prevalent.currentOnlineUsers.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        count_item--;
                                                        Log.d("count", String.valueOf(count_item));
                                                        NoItemCart();
                                                        checkSidUpdate();
                                                        Toast.makeText(CartActivity.this, "Item removed Successfully", Toast.LENGTH_SHORT).show();

                                                        onStart();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerview.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                add = data.getStringExtra("result");
                Intent intent =new Intent(CartActivity.this, ConferFinalOrderActivity.class);
              // Log.d("jdj",data.getStringExtra("lattitude"));
               // Log.d("jdj",data.getStringExtra("longitude"));
               // intent.putExtra("sid",sid);
                intent.putExtra("deslat",data.getStringExtra("lattitude"));
                intent.putExtra("deslon",data.getStringExtra("longitude"));
                intent.putExtra("TotalPrice",data.getIntExtra("TotalPrice", 99999));
                Log.d("totalllCarttoConfirm",String.valueOf(data.getIntExtra("TotalPrice", 99999)));
                intent.putExtra("Address",add );
                intent.putExtra("sid",sid);
                startActivity(intent);
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overallAmount = 0;
count_item = -1;
        NoItemCart();
    }

    public void NoItemCart() {

        if(count_item == -1) {
            Processbtn.setVisibility(View.GONE);
            txtmsg1.setVisibility(View.VISIBLE);
            txtmsg1.setText("No item in cart");
        }
        else {

            Processbtn.setVisibility(View.VISIBLE);
            txtmsg1.setVisibility(View.GONE);

        }
    }

    public void checkSidUpdate() {
        if(count_item == -1) {
            final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("sidRef");

            cartListRef.child(Prevalent.currentOnlineUsers.getPhone())
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                NoItemCart();
                            }
                        }
                    });
        }
    }
}






