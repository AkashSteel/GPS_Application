package com.example.akash.gps_application;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView lat;
    TextView lon;
    TextView distance;
    public TextView add;
    String latitude;
    String longitude;
    Location old;
    double dist;
    double [] init = new double[2];
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lat = (TextView)findViewById(R.id.Lat);
        lon = (TextView)findViewById(R.id.Long);
        add = (TextView)findViewById(R.id.address);
        distance = (TextView)findViewById(R.id.dist);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener location = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latit = location.getLatitude();
                double longit = location.getLongitude();
                latitude = Double.toString(latit);
                longitude = Double.toString(longit);
                if(init[0]== 0||init[1]==0){
                    init[0] = latit;
                    init[1] = longit;
                    old = location;
                }
                lon.setText("Longitude:\n "+longit);
                lat.setText("Latitude:\n "+latit);

                locationAccess loc = new locationAccess();
                loc.execute(latit,longit);

                double meters = location.distanceTo(old)*0.000621371192;
                dist+= meters;
                old=location;
                distance.setText(dist+" \nMiles");

                LatLng anyloc = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(anyloc).title("current location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(anyloc));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResuly
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1/2, location);
            //locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public class locationAccess extends AsyncTask<Double , Void , Void> {
        public String st="";
        @Override
        protected Void doInBackground(Double... doubles) {
            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng="+doubles[0]+","+doubles[1]+"&key=AIzaSyD5p0uBr6FapZFYG8OmWT-AmSs4A6-qO24");
                URL url2 = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng="+doubles[0]+","+doubles[1]+"&key=AIzaSyB0Sa4k5ZI7SnnjmrpL9d1kqvJi0Hv1G2M");
                URLConnection urlConnection = url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(reader);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Log.d("st",st);
                JSONObject maicn = new JSONObject(st);
                JSONArray results = maicn.getJSONArray("results");
                JSONObject current = results.getJSONObject(0);
                String ad = current.getString("formatted_address");
                add.setText(ad);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}