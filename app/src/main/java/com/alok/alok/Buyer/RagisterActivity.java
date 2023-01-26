package com.alok.alok.Buyer;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alok.alok.Map.MapActivity;
import com.alok.alok.Notification.SendNotificationPack.Token;
import com.alok.alok.R;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RagisterActivity extends AppCompatActivity {
    private EditText nameInput,phoneInput,emailInput,passwordInput,addressInput;
    private Button registerbtn,sellerLoc;


    private FirebaseAuth auth;
    private ProgressDialog loadingBar;

    int LAUNCH_SECOND_ACTIVITY = 2;


    String lattitude, longitude;

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
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
                            Intent intent = new Intent(RagisterActivity.this, LoginActivity.class);
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
        setContentView(R.layout.activity_ragister);
        checkConnection();
        auth = FirebaseAuth.getInstance();

        nameInput=findViewById(R.id.seller_name);
        phoneInput=findViewById(R.id.seller_phone);
        passwordInput=findViewById(R.id.seller_password);
        addressInput=findViewById(R.id.seller_address);
        emailInput=findViewById(R.id.seller_email);
        registerbtn=findViewById(R.id.seller_register_btn);
        loadingBar=new ProgressDialog(this);
        sellerLoc=findViewById(R.id.sellerLoc);

        sellerLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RagisterActivity.this, MapActivity.class);
                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        });
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSeller();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void registerSeller() {
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String address = addressInput.getText().toString();

        if(!name.equals("")&& !phone.equals("")&& !email.equals("") && !password.equals("")&& !address.equals("") && lattitude != null && longitude != null) {
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least six characters long ", Toast.LENGTH_SHORT).show();

            } else {
                loadingBar.setTitle("creating User account");
                loadingBar.setMessage("please wait while we are checking credentials.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                checkConnection();
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final DatabaseReference rootRef;
                                    rootRef = FirebaseDatabase.getInstance().getReference();

                                    String uid = auth.getCurrentUser().getUid();
                                    HashMap<String, Object> sellerMap = new HashMap<>();
                                    sellerMap.put("uid", uid);
                                    sellerMap.put("phone", phone);
                                    sellerMap.put("email", email);
                                    sellerMap.put("address", address);
                                    sellerMap.put("delAddress",address);
                                    sellerMap.put("delLattitude",lattitude);
                                    sellerMap.put("delLongitude", longitude);
                                    sellerMap.put("name", name);
                                    sellerMap.put("password", password);
//                                Log.d("latt",lattitude);
//                                Log.d("lattt",longitude);
                                    sellerMap.put("lattitude", lattitude);
                                    sellerMap.put("longitude", longitude);

                                    rootRef.child(
                                            "Users"
                                    ).child(uid).updateChildren(sellerMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    loadingBar.dismiss();
                                                    Toast.makeText(RagisterActivity.this, "You are registered successfully", Toast.LENGTH_SHORT).show();

                                                    UpdateToken();

                                                    Intent intent = new Intent(RagisterActivity.this, LoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            checkConnection();
                                            Toast.makeText(RagisterActivity.this, "Error ! Retry", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                                else
                                {
                                    loadingBar.dismiss();
                                    if(task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                        Toast.makeText(RagisterActivity.this, "The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(RagisterActivity.this, "Kindly check email and password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        }
        else
        {
            if(name.equals(""))
            {
                Toast.makeText(RagisterActivity.this, "please enter name",Toast.LENGTH_SHORT).show();
            }
            else if (phone.equals("")) {
                Toast.makeText(RagisterActivity.this, "please enter phone no",Toast.LENGTH_SHORT).show();
            }
            else if (email.equals("")) {
                Toast.makeText(RagisterActivity.this, "please enter email",Toast.LENGTH_SHORT).show();
            }

            else if (password.equals("")) {
                Toast.makeText(RagisterActivity.this, "please enter password",Toast.LENGTH_SHORT).show();
            }
            else if (address.equals("")) {
                Toast.makeText(RagisterActivity.this, "please enter address",Toast.LENGTH_SHORT).show();
            }
            else if (lattitude == null && longitude == null ) {
                Toast.makeText(RagisterActivity.this, "please select locaion on Map",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void UpdateToken(){
        checkConnection();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("BuyerTokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == RESULT_OK){
                addressInput.setText(data.getStringExtra("result"));
                lattitude = data.getStringExtra("lattitude");
                longitude = data.getStringExtra("longitude");
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}






























/*

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alok.alok.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RagisterActivity extends AppCompatActivity {
    private Button createaccountbutton;
    private EditText Inputname,InputPhoneno,InputPassword,InputEmail;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ragister);
        createaccountbutton=(Button)findViewById(R.id.main_start_register);
        Inputname=(EditText)findViewById(R.id.register_username_input);
        InputPhoneno=(EditText)findViewById(R.id.register_phone_number_input);
        InputPassword=(EditText)findViewById(R.id.register_password_input);
        loadingBar=new ProgressDialog(this);
        InputEmail=(EditText)findViewById(R.id.register_email_input);
        createaccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String name=Inputname.getText().toString();
        String phone=InputPhoneno.getText().toString();
        String password=InputPassword.getText().toString();
        String email=InputEmail.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please choose your password...", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please choose your password...", Toast.LENGTH_SHORT).show();
        }
       else{
           loadingBar.setTitle("create account");
           loadingBar.setMessage("please wait while we are checking credentials.");
           loadingBar.setCanceledOnTouchOutside(false);
           loadingBar.show();

           validatePhoneNumber(name,phone,password,email);
        }
    }

    private void validatePhoneNumber(final String name, final String phone, final String password, final String email) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.child("Users").child(phone).exists()){
                    HashMap<String,Object>userdataMap=new HashMap<>();
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    userdataMap.put("name",name);
                    userdataMap.put("email",email);
                    userdataMap.put("lattitude",getIntent().getStringExtra("lattitude"));
                    userdataMap.put("longitude",getIntent().getStringExtra("longitude"));
                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RagisterActivity.this, "Congractulations! your account has been created", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RagisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        loadingBar.dismiss();
                                        Toast.makeText(RagisterActivity.this, "Network Error ! please try again...", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(RagisterActivity.this, "This"+phone+"Number already exits.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RagisterActivity.this, "Please try with another phone number", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RagisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
*/
