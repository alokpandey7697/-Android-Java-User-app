package com.alok.alok.Sort;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.alok.alok.Buyer.storeList;
import com.alok.alok.models.shopModel;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

public class SortPlaces implements Comparator<shopModel> {
    LatLng currentLoc;
    public SortPlaces(LatLng current){
        currentLoc = current;
    }
    @Override
    public int compare(final shopModel place1, final shopModel place2) {
        double lat1 = Double.parseDouble(place1.getLattitude());
        double lon1 = Double.parseDouble(place1.getLongitude());
        double lat2 = Double.parseDouble(place2.getLattitude());
        double lon2 = Double.parseDouble(place2.getLongitude());

        double distanceToPlace1 = distance(currentLoc.latitude, currentLoc.longitude, lat1, lon1);
        double distanceToPlace2 = distance(currentLoc.latitude, currentLoc.longitude, lat2, lon2);
        return (int) (distanceToPlace1 - distanceToPlace2);
    }

    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, *in meters*
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }

}