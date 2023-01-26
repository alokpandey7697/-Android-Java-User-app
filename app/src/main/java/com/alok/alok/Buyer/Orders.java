package com.alok.alok.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alok.alok.Admin.AdminUserProductActivity;
import com.alok.alok.Prevalent.Prevalent;
import com.alok.alok.R;
import com.alok.alok.models.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Orders extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference ordersRef,adminviewref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        //adminviewref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin view");

        orderList=findViewById(R.id.orders_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));
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
                            Intent intent = new Intent(Orders.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }


    public boolean isValidNumeric(String str)
    {
        str = str.trim(); // trims the white spaces.

        if (str.length() == 0)
            return false;

        // if string is of length 1 and the only
        // character is not a digit
        if (str.length() == 1 && !Character.isDigit(str.charAt(0)))
            return false;

        // If the 1st char is not '+', '-', '.' or digit
        if (str.charAt(0) != '+' && str.charAt(0) != '-'
                && !Character.isDigit(str.charAt(0))
                && str.charAt(0) != '.')
            return false;

        // To check if a '.' or 'e' is found in given
        // string. We use this flag to make sure that
        // either of them appear only once.
        boolean flagDotOrE = false;

        for (int i = 1; i < str.length(); i++) {
            // If any of the char does not belong to
            // {digit, +, -, ., e}
            if (!Character.isDigit(str.charAt(i))
                    && str.charAt(i) != 'e' && str.charAt(i) != '.'
                    && str.charAt(i) != '+' && str.charAt(i) != '-')
                return false;

            if (str.charAt(i) == '.') {
                // checks if the char 'e' has already
                // occurred before '.' If yes, return 0.
                if (flagDotOrE == true)
                    return false;

                // If '.' is the last character.
                if (i + 1 >= str.length())
                    return false;

                // if '.' is not followed by a digit.
                if (!Character.isDigit(str.charAt(i + 1)))
                    return false;
            }

            else if (str.charAt(i) == 'e') {
                // set flagDotOrE = 1 when e is encountered.
                flagDotOrE = true;

                // if there is no digit before 'e'.
                if (!Character.isDigit(str.charAt(i - 1)))
                    return false;

                // If 'e' is the last Character
                if (i + 1 >= str.length())
                    return false;

                // if e is not followed either by
                // '+', '-' or a digit
                if (!Character.isDigit(str.charAt(i + 1))
                        && str.charAt(i + 1) != '+'
                        && str.charAt(i + 1) != '-')
                    return false;
            }
        }

        /* If the string skips all above cases, then
           it is numeric*/
        return true;
    }


    protected  void onStart()
    {
        super.onStart();
        checkConnection();
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()),AdminOrders.class)
                        .build();
        FirebaseRecyclerAdapter<AdminOrders, Orders.AdminOrdersViewHolder> adapter= new FirebaseRecyclerAdapter<AdminOrders, Orders.AdminOrdersViewHolder>(options) {

            @NonNull
            @Override
            public Orders.AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_orders_seller_layout, parent, false);
                return new Orders.AdminOrdersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull Orders.AdminOrdersViewHolder holder, int position, @NonNull AdminOrders model) {
                holder.userName.setText("Name:" + model.getName());
                holder.UserPhoneNumber.setText("Phone:" + model.getPhone());
                holder.userTotalPrice.setText("Total Amount:" + model.getTotalAmount());
                holder.userDateTime.setText("Order at:" + model.getDate() + " " + model.getTime());
                holder.userShippingAddress.setText("Shipping Address:" + model.getAddress() + " " + model.getCity());
                holder.orderState.setText("Status: "+model.getState());
                if (model.getDeliCharge() == null) {
                    holder.delCharge.setText("Delivery Charge :" + " Not added  yet");
                } else if(model.getDeliCharge().equals("Not added")) {
                    holder.delCharge.setText("Delivery Charge :" + " Not added  yet");
                }
                else if(isValidNumeric(model.getDeliCharge())){
                    holder.delCharge.setText("Delivery Charge :" + "  ₹ " + model.getDeliCharge());
                }                holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uID = getRef(position).getKey();
                        Intent intent = new Intent(Orders.this, AdminUserProductActivity.class);
                        intent.putExtra("deslat", model.getLattitude());
                        intent.putExtra("deslon", model.getLongitude());
                        intent.putExtra("uid", uID);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
                orderList.setAdapter(adapter);
                adapter.startListening();
            }



public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder

{
    public Button showOrderBtn;
    public TextView userName,UserPhoneNumber, userTotalPrice, userDateTime, userShippingAddress,orderState,delCharge;
    public AdminOrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        delCharge = itemView.findViewById(R.id.delCharge);
        userName=itemView.findViewById(R.id.user_name);
        UserPhoneNumber=itemView.findViewById(R.id.order_phone_number);
        userTotalPrice=itemView.findViewById(R.id.order_total_price);
        userDateTime=itemView.findViewById(R.id.order_date_time);
        userShippingAddress=itemView.findViewById(R.id.order_address_city);
        showOrderBtn=itemView.findViewById(R.id.show_all_products_btn);
        orderState=(TextView)itemView.findViewById(R.id.order_state);
    }
}
}