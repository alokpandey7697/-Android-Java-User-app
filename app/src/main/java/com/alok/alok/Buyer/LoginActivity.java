package com.alok.alok.Buyer;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alok.alok.Map.LaunchMap;
import com.alok.alok.Notification.SendNotificationPack.Token;
import com.alok.alok.Prevalent.Prevalent;
import com.alok.alok.R;
import com.alok.alok.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {


    private Button login, register;
    private EditText emailInput, passwordInput;
    private ProgressDialog loadingBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
checkConnection();
        auth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.seller_login_email);
        passwordInput = findViewById(R.id.seller_login_password);
        login = findViewById(R.id.seller_login_btn);
        register = findViewById(R.id.seller_register_btn);
        loadingBar = new ProgressDialog(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginSeller();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RagisterActivity.class);
                startActivity(intent);
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
                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            loadingBar.setTitle("logging you...");
            loadingBar.setMessage("please wait while we are checking credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            getUserData();
           /* Intent intent = new Intent(LoginActivity.this, storeList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();*/
        }
    }

    private void LoginSeller() {

        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (!email.equals("") && !password.equals("")) {

            loadingBar.setTitle("logging you...");
            loadingBar.setMessage("please wait while we are checking credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
checkConnection();
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                getUserData();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Please check email and password", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            checkConnection();
            Toast.makeText(this, "Please check email and password", Toast.LENGTH_SHORT).show();
        }
    }

    public void getUserData() {
        checkConnection();
        final DatabaseReference RootRef;
        final Users[] usersdata = new Users[1];
        String sid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Users").child(sid).exists()) {
                    Prevalent.currentOnlineUsers = dataSnapshot.child("Users").child(sid).getValue(Users.class);
                    UpdateToken();
                    loadingBar.dismiss();
                    Intent intent = new Intent(LoginActivity.this, LaunchMap.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                  /*  Intent intent = new Intent(LoginActivity.this, storeList.class);
                    loadingBar.dismiss();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();*/
                } else {
                    Toast.makeText(LoginActivity.this, "Please check email and password", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    FirebaseAuth.getInstance().signOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                checkConnection();
            }
        });
    }

    private void UpdateToken() {
        checkConnection();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("BuyerTokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }
}


