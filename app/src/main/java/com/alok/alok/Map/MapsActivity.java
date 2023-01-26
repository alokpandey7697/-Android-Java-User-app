package com.alok.alok.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.alok.alok.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList markerPoints= new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

        // mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
        //  @Override
        //public void onMapClick(LatLng latLng) {

//                if (markerPoints.size() > 1) {
//                    markerPoints.clear();
//                    mMap.clear();
//                }
        LatLng latLng[] = new LatLng[2];
        Double orlat, orlon, deslat, deslon;

//        Log.d("value",getIntent().getStringExtra("deslat"));
//        Log.d("value",getIntent().getStringExtra("deslon"));
//        Log.d("value",getIntent().getStringExtra("orlat"));
//        Log.d("value",getIntent().getStringExtra("orlon"));
        orlat=Double.valueOf(getIntent().getStringExtra("orlat"));
        orlon=Double.valueOf(getIntent().getStringExtra("orlon"));
        deslat=Double.valueOf(getIntent().getStringExtra("deslat"));
        deslon=Double.valueOf(getIntent().getStringExtra("deslon"));

         latLng[0] = new LatLng(orlat,orlon);
         latLng[1] = new LatLng(deslat,deslon);
//        latLng[0] = new LatLng(31.99, 145.77);
//
//        latLng[1] = new LatLng(51.99, 95.77);

        for (int i = 0; i < 2; i++) {

            // Adding new item to the ArrayList
            markerPoints.add(latLng[i]);

            // Creating MarkerOptions
            MarkerOptions options = new MarkerOptions();

            // Setting the position of the marker
            options.position(latLng[i]);

            if (markerPoints.size() == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (markerPoints.size() == 2) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

            // Add new marker to the Google Map Android API V2
            mMap.addMarker(options);

            // Checks, whether start and end locations are captured
            if (markerPoints.size() >= 2) {
                LatLng origin = (LatLng) markerPoints.get(0);
                LatLng dest = (LatLng) markerPoints.get(1);

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
        }


   // });


    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";


            try {
                data = downloadUrl(url[0]);
                //Log.d("Background Task",   data);
            } catch (Exception e) {
               // Log.d("Background Task", e.toString() + data);
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
if(result != "") {
    ParserTask parserTask = new ParserTask();


    parserTask.execute(result);
}
else {
    return;
}
        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {

                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    routes = parser.parse(jObject);

            } catch (Exception e) {
                //Log.d("kerrr",e.getMessage());
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            //Log.d("hhh",String.valueOf(result.size()));
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            //if(lineOptions != null)
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
       // String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
      String url =   "https://api.tomtom.com/routing/1/calculateRoute/"+str_origin+":"+str_dest+"/json?maxAlternatives=1&avoid=unpavedRoads&key=LV4bPaBWOiSmFG4GBf0N4Iy1sqXGOT7O";
      //  https://api.tomtom.com/routing/1/calculateRoute/52.50931%2C13.42936%3A52.50274%2C13.43872/json?maxAlternatives=1&avoid=unpavedRoads&key=*****
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //Log.d("Exceptioon", e.toString());
            this.runOnUiThread(new Runnable() {
                public void run() {
                    //Toast.makeText(MapsActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                    CharSequence options[] = new CharSequence[]
                            {
                                    "ok"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle(("No way found ? "));

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }


                    });
                    builder.show();
                }
            });
            return data;
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
