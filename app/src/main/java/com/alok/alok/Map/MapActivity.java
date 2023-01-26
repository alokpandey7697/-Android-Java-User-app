package com.alok.alok.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alok.alok.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textLatLong, textAddress;
    private ProgressBar progressBar;
    private ResultReceiver resultReceiver;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private LatLng latLng;
    private Button confirm;
    private String add;
    private double lattitude, longitude;
    private int overallAmount;
private boolean first = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        resultReceiver = new AddresssResultReceiver(new Handler());
        textLatLong = findViewById(R.id.textLatLong);
        progressBar = findViewById(R.id.progressBar);
        textAddress = findViewById(R.id.textAdress);
        confirm = findViewById(R.id.confirm);
        overallAmount = getIntent().getIntExtra("TotalPrice",999999);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent =new Intent(MapActivity.this, ConferFinalOrderActivity.class);
//                intent.putExtra("Total price",getIntent().getStringExtra("Total price"));
//                intent.putExtra("Address",add );
//                startActivity(intent);
//                finish();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("lattitude", String.valueOf(lattitude));
                returnIntent.putExtra("longitude", String.valueOf(longitude));
                returnIntent.putExtra("result", add);
                returnIntent.putExtra("TotalPrice", overallAmount);
                Log.d("totalMap",String.valueOf(overallAmount));
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        findViewById(R.id.buttonGetCurrentLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MapActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                } else {
                    getCurrentLocation();
                }

            }
        });

getCurrentLocation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Location Permission denied ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getCurrentLocation() {

        progressBar.setVisibility(View.VISIBLE);

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MapActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(MapActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MapActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            lattitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();


                            latLng = new LatLng(lattitude, longitude);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                            textLatLong.setText(
                                    String.format(
                                            "Latitude: %s\nLongitude: %s",
                                            lattitude,
                                            longitude
                                    )
                            );

                            Location location = new Location("providerNA");
                            location.setLatitude(lattitude);
                            location.setLongitude(longitude);
                            fetchAddressFromLatLong(location);
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }


                    }
                }, Looper.getMainLooper());
    }

    private void fetchAddressFromLatLong(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng latLng = googleMap.getCameraPosition().target;
                textLatLong.setText(
                        String.format(
                                "Latitude: %s\nLongitude: %s",
                                latLng.latitude,
                                latLng.longitude
                        ));
                Location location = new Location("providerNA");

                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                lattitude = location.getLatitude();
                longitude = location.getLongitude();
                fetchAddressFromLatLong(location);
            }
        });

    }

    private class AddresssResultReceiver extends ResultReceiver {

        AddresssResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode == Constants.SUCESS_RESULT) {
                textAddress.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                add = resultData.getString(Constants.RESULT_DATA_KEY);
            } else {
               // Log.d("alok","kdjfk");

                if(first == true) {
                    first = false;
                }
                else {                Toast.makeText(MapActivity.this, "Retry ", Toast.LENGTH_SHORT).show();
                }

            }
            progressBar.setVisibility(View.GONE);
        }
    }
}
