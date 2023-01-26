package com.alok.alok.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alok.alok.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    private EditText otp;
    private Button submit;
    private TextView resend;
    private MKLoader loader;
    private String number,id;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        otp = findViewById(R.id.otp);
        submit = findViewById(R.id.submit);
        resend = findViewById(R.id.resend);
        loader = findViewById(R.id.loader);


//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("result","SUCESS");
//        setResult(RESULT_OK,returnIntent);
//        finish();


        //mAuth = FirebaseAuth.getInstance();
        number = getIntent().getStringExtra("number");

        sendVerificationCode();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(otp.getText().toString())){
                    Toast.makeText(VerificationActivity.this, "Enter Otp", Toast.LENGTH_SHORT).show();
                }
                else if(otp.getText().toString().replace(" ","").length()!=6){
                    Toast.makeText(VerificationActivity.this, "Enter right otp", Toast.LENGTH_SHORT).show();
                }
                else {
                    loader.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otp.getText().toString().replace(" ",""));
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });

    }

    private void sendVerificationCode() {

        new CountDownTimer(60000,1000){
            @Override
            public void onTick(long l) {
               resend.setText(""+l/1000);
               resend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resend.setText(" Resend");
                resend.setEnabled(true);
            }
        }.start();


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerificationActivity.this.id = id;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerificationActivity.this, "Failed"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });        // OnVerificationStateChangedCallbacks


    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result","SUCESS");
                            setResult(RESULT_OK,returnIntent);
                            finish();

    }


}
