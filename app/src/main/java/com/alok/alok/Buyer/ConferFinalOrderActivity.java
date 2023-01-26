package com.alok.alok.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alok.alok.Notification.SendNotif;
import com.alok.alok.Prevalent.Prevalent;
import com.alok.alok.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConferFinalOrderActivity extends AppCompatActivity {

    private EditText nameEdittext,phoneEdittxt,addressEdittxt,cityedittxt;
    private Button confirmOrder;
    private String totalAmount="";
    private String pid;
    private String sid;
    private String pending;
    private String accepted;
    private String delivered;
    private String rejected;
    private String sellerHistory;
    private String buyerHistory;


    final String savecurrentDate,saveCurrentTime;
    public ConferFinalOrderActivity(){
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        savecurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calForDate.getTime());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confer_final_order);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
checkConnection();
        totalAmount=String.valueOf(getIntent().getIntExtra("TotalPrice",99999));
        Log.d("overallConfirm",totalAmount);
        sid = getIntent().getStringExtra("sid");
        Toast.makeText(this, "Total Price = ₹ "+totalAmount, Toast.LENGTH_SHORT).show();

        confirmOrder  =(Button)findViewById(R.id.Confirm_order_btn);
        nameEdittext=(EditText)findViewById(R.id.shipment_name);
        phoneEdittxt=(EditText)findViewById(R.id.shipment_phone_number);
        addressEdittxt=(EditText)findViewById(R.id.shipment_address);
        cityedittxt=(EditText)findViewById(R.id.shipment_city);

        addressEdittxt.setText(getIntent().getStringExtra("Address"));

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Check();
            }
        });
    }

    private void Check() {
        if(TextUtils.isEmpty(nameEdittext.getText().toString()))
        {
            Toast.makeText(this, "Please provide your full name", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(phoneEdittxt.getText().toString()))
        {
            Toast.makeText(this, "Please provide your phone number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEdittxt.getText().toString()))
        {
            Toast.makeText(this, "Please provide your full address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cityedittxt.getText().toString()))
        {
            Toast.makeText(this, "Please enter your city name", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ConfirmOrder();
        }
    }
    private void updateDelAddress(String delAddress, String delLongitude, String delLattitude)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> sellerMap = new HashMap<>();
        sellerMap.put("delAddress",delAddress);
        sellerMap.put("delLattitude",delLattitude);
        sellerMap.put("delLongitude", delLongitude);
        ref.child(FirebaseAuth.getInstance().getUid()).updateChildren(sellerMap);

    }
    private void ConfirmOrder() {
        final DatabaseReference orderRef= FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUsers.getPhone()+savecurrentDate+saveCurrentTime);
        HashMap<String,Object>orderMap=new HashMap<>();
        orderMap.put("totalAmount",totalAmount);
        orderMap.put("name",nameEdittext.getText().toString());
        orderMap.put("phone",phoneEdittxt.getText().toString());
        orderMap.put("address",addressEdittxt.getText().toString());
        orderMap.put("city",cityedittxt.getText().toString());
        orderMap.put("date",savecurrentDate);
        orderMap.put("time",saveCurrentTime);
        orderMap.put("lattitude",getIntent().getStringExtra("deslat"));
        orderMap.put("longitude",getIntent().getStringExtra("deslon"));
        orderMap.put("state","not shipped");
        orderMap.put("sid",sid);
        orderMap.put("pending",sid+" "+"Y");
        orderMap.put("accepted","N");
        orderMap.put("rejected","N");
        orderMap.put("delivered","N");
        orderMap.put("deliveryBoy","N");

        orderMap.put("pend","Y");

        orderMap.put("acce","N");

        orderMap.put("reje","N");

        orderMap.put("deli","N");

        orderMap.put("deliBoy","N");

        orderMap.put("uid", Prevalent.currentOnlineUsers.getUid());


        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {   copyFirebaseData();
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User view")
                            .child(Prevalent.currentOnlineUsers.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        updateDelAddress(addressEdittxt.getText().toString(),getIntent().getStringExtra("deslat"), getIntent().getStringExtra("deslon"));
                                       /* SharedPreferences sp = getSharedPreferences("ADDRESS", MODE_PRIVATE);
                                        SharedPreferences.Editor edit = sp.edit();
                                        edit.putString("lattitude", getIntent().getStringExtra("deslat"));
                                        edit.putString("longitude", getIntent().getStringExtra("deslon"));
                                        edit.putString("add",addressEdittxt.getText().toString());
                                        edit.apply();*/
                                        SendNotif.sendNotification("SellerTokens",sid,"VireStore","You have a new Pending order","sender", getApplicationContext());

                                        checkSidUpdate();

                                        Toast.makeText(ConferFinalOrderActivity.this, " order placed successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(ConferFinalOrderActivity.this, storeList.class);

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    finish();
                                    }
                                }
                            });
                }
            }
        });

    }


    public void copyFirebaseData() {

        DatabaseReference questionNodes = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User view").child(Prevalent.currentOnlineUsers.getPhone()).child("Products");
        final DatabaseReference toUsersQuestions = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin view").child(Prevalent.currentOnlineUsers.getPhone()+savecurrentDate+saveCurrentTime).child("Products");

        questionNodes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot questionCode : dataSnapshot.getChildren()) {
                    String pid = questionCode.getKey();
                    String imageUrlString = questionCode.child("date").getValue(String.class);
                    assert pid != null;
                    toUsersQuestions.child(pid).child("date").setValue(imageUrlString);
                    imageUrlString = questionCode.child("discount").getValue(String.class);
                    toUsersQuestions.child(pid).child("discount").setValue(imageUrlString);
                    imageUrlString = questionCode.child("image").getValue(String.class);
                    toUsersQuestions.child(pid).child("image").setValue(imageUrlString);
                    imageUrlString = questionCode.child("pid").getValue(String.class);
                    toUsersQuestions.child(pid).child("pid").setValue(imageUrlString);
                    imageUrlString = questionCode.child("pname").getValue(String.class);
                    toUsersQuestions.child(pid).child("pname").setValue(imageUrlString);
                    imageUrlString = questionCode.child("price").getValue(String.class);
                    toUsersQuestions.child(pid).child("price").setValue(imageUrlString);
                    imageUrlString = questionCode.child("quantity").getValue(String.class);
                    toUsersQuestions.child(pid).child("quantity").setValue(imageUrlString);
                    imageUrlString = questionCode.child("sellerAddress").getValue(String.class);
                    toUsersQuestions.child(pid).child("sellerAddress").setValue(imageUrlString);
                    imageUrlString = questionCode.child("time").getValue(String.class);
                    toUsersQuestions.child(pid).child("time").setValue(imageUrlString);
                    imageUrlString = questionCode.child("sid").getValue(String.class);
                    toUsersQuestions.child(pid).child("sid").setValue(imageUrlString);
                    imageUrlString = questionCode.child("lattitude").getValue(String.class);
                    toUsersQuestions.child(pid).child("lattitude").setValue(imageUrlString);
                    imageUrlString = questionCode.child("longitude").getValue(String.class);
                    toUsersQuestions.child(pid).child("longitude").setValue(imageUrlString);
                    imageUrlString = questionCode.child("uid").getValue(String.class);
                    toUsersQuestions.child(pid).child("uid").setValue(imageUrlString);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkSidUpdate() {

            final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("sidRef");

            cartListRef.child(Prevalent.currentOnlineUsers.getPhone())
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    checkSidUpdate();
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
                            Intent intent = new Intent(ConferFinalOrderActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }
}


