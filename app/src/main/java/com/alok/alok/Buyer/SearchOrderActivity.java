package com.alok.alok.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alok.alok.Adapter.MyRecyclerViewAdapter;
import com.alok.alok.Adapter.MyRecyclerViewAdapterProductSearch;
import com.alok.alok.R;
import com.alok.alok.Sort.SortPlaces;
import com.alok.alok.ViewHolder.ProductViewHolder;
import com.alok.alok.models.Products;
import com.alok.alok.models.shopModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SearchOrderActivity extends AppCompatActivity {

    private Button SearchBtn;
    private EditText inputtxt;
    private String SearchInput;
    private RecyclerView searchList;
    DatabaseReference reference;
    ArrayList<Products> list;
    MyRecyclerViewAdapterProductSearch adapter;


    public SearchOrderActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_search_order);
        inputtxt=findViewById(R.id.search_product_name);
        SearchBtn=findViewById(R.id.searchBtn);
        searchList=findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchOrderActivity.this));

        SearchBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SearchInput=inputtxt.getText().toString();
                onStart();
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
                            Intent intent = new Intent(SearchOrderActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }
    protected void onStart()
    {


        super.onStart();


        checkConnection();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SearchOrderActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }

        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(this));

        reference = FirebaseDatabase.getInstance().getReference().child("products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Products p = dataSnapshot1.getValue(Products.class);
                    if(p.getSid().equals(getIntent().getExtras().getString("sid"))) {
                        if (SearchInput != null ){
                            if(SearchInput.length() > 0) {
                            if(p.getPname().toLowerCase().contains(SearchInput.toLowerCase()))
                            list.add(p);
                        }
                    } else {
                            list.add(p);
                        }
                }
                }
                adapter = new MyRecyclerViewAdapterProductSearch(SearchOrderActivity.this, list);
                searchList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                checkConnection();
                Toast.makeText(SearchOrderActivity.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
