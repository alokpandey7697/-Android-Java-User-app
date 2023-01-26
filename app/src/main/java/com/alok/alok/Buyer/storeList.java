package com.alok.alok.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alok.alok.Adapter.MyRecyclerViewAdapter;
import com.alok.alok.Map.MapActivity;
import com.alok.alok.Prevalent.Prevalent;
import com.alok.alok.R;
import com.alok.alok.Sort.SortPlaces;
import com.alok.alok.models.Users;
import com.alok.alok.models.shopModel;
import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class storeList extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, NavigationView.OnNavigationItemSelectedListener{

    DatabaseReference reference;
    ArrayList<shopModel> list;
    MyRecyclerViewAdapter adapter;
    LatLng latLng;
    boolean first = true, set = true;
    LocationManager locationManager;
    TextView userNameTextView;
    CircleImageView profileImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Virestore");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
      userNameTextView = headerView.findViewById(R.id.user_profile_name);
       profileImageView = headerView.findViewById(R.id.user_profile_image);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(storeList.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(storeList.this, CartActivity.class);
                    startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                    new AlertDialog.Builder(this)
                            .setMessage("Location Permission Denied")
                            .setPositiveButton("Retry later!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(storeList.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
                return;
            }

        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        getUserData();

        checkConnection();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(storeList.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }

      //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
       // recyclerView.setLayoutManager(new LinearLayoutManager(this));
recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        reference = FirebaseDatabase.getInstance().getReference().child("Seller");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    shopModel p = dataSnapshot1.getValue(shopModel.class);
                    list.add(p);
                }

                latLng = new LatLng(Prevalent.currentOnlineUsersLocation.getLattitude(),Prevalent.currentOnlineUsersLocation.getLongitude());
                if (latLng != null) {
                    Collections.sort(list, new SortPlaces(latLng));
                }

                adapter = new MyRecyclerViewAdapter(storeList.this, list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                checkConnection();
                Toast.makeText(storeList.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
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
                            Intent intent = new Intent(storeList.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {


            Intent intent = new Intent(storeList.this, CartActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_search) {
if(Prevalent.currentOnlineUsers != null) {
    Intent intent = new Intent(storeList.this, SearchOrderActivityshop.class);
    startActivity(intent);
}

        }
        // else if (id == R.id.nav_categories)
        //  {

        //  }
        else if (id == R.id.nav_settings) {


            Intent intent = new Intent(storeList.this, SettingsActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(storeList.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_orders) {

            Intent intent = new Intent(storeList.this, Orders.class);
            startActivity(intent);


        } else if (id == R.id.customer_service) {

          /*  Intent intent = new Intent(storeList.this, customer_service.class);
            startActivity(intent);*/
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle("Customer Care")
                    .setMessage("For any query mail us at xyz@gmail.com")
                    .addButton("Ok", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialog, which) -> {

                        dialog.dismiss();
                    });

// Show the alert
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

                if (dataSnapshot.child("Users").child(sid).exists() ) {
                    if( set == true) {
                        Prevalent.currentOnlineUsers = dataSnapshot.child("Users").child(sid).getValue(Users.class);
                        getUserData();
                        userNameTextView.setText(Prevalent.currentOnlineUsers.getName());

                        Glide.with(profileImageView).load(Prevalent.currentOnlineUsers.getImage()).placeholder(R.drawable.profile).into(profileImageView);
                    set = false;
                    }
                } else {
                    Toast.makeText(storeList.this, "Create Account first", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(storeList.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                checkConnection();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}