package com.alok.alok.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alok.alok.Buyer.HomeActivity;
import com.alok.alok.Buyer.LoginActivity;
import com.alok.alok.R;

public class AdminHomeActivity extends AppCompatActivity {

     private Button LogOutBtn, checkOrdersbtn,maintainProductsBtn,approveProducts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        LogOutBtn = findViewById(R.id.logout_admin);
        checkOrdersbtn=findViewById(R.id.check_orders);
        maintainProductsBtn=findViewById(R.id.maintain_btn);
        approveProducts=findViewById(R.id.check_approve_orders);


        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);

            }
        });

        LogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkOrdersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this, AdminNewActivity.class);
                startActivity(intent);

            }
        });
        approveProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this, CheckNewProductsActivity.class);
                startActivity(intent);

            }
        });
    }
}
