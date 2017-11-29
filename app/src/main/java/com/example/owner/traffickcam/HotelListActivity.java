package com.example.owner.traffickcam;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HotelListActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    ListView mList;
    List<String> nearbyHotels;
    private ArrayAdapter<String> mAdapter;
    int PROXIMITY_RADIUS = 10000;
    double latitude = 39.981,longitude = -75.156;
    String url;
    Context context;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    public static final int REQUEST_LOCATION_CODE = 99;
    EditText mEditText;
    private LocationListener mLocListener;
    List<HashMap<String, String>> googleHotels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        checkLocationPermission();

        initmList();
        initmLocListener();

        mEditText = (EditText) findViewById(R.id.locationText);
        nearbyHotels = new ArrayList<String>();
        context = this;


//        searchNearbyHotels();
    }

    private void initmList()
    {
        mList = (ListView) findViewById(R.id.hotel_list);
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object listItem = mList.getItemAtPosition(position);
                hotelInfoActivity();
                // todo
            }
        });
    }

    private void hotelInfoActivity()
    {
        Intent intent = new Intent(this, HotelInfoActivity.class);
        startActivity(intent);
    }


    private void initmLocListener(){

        mLocListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        };
    }


    public void searchOnClick(View view)
    {
        String keywords = mEditText.getText().toString().replace(" ", "%20");
        searchNearbyHotels(keywords);
    }

    private void searchNearbyHotels()
    {
        HotelData getNearbyPlacesData = new HotelData();

        url = getUrl(latitude, longitude, "lodging");

        getNearbyPlacesData.execute();
    }

    private void searchNearbyHotels(String keywords)
    {
        HotelData getNearbyPlacesData = new HotelData();

        url = getUrl(latitude, longitude, keywords, "lodging");

        getNearbyPlacesData.execute();
    }

    private void updateLatLon()
    {
        try{
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            String latlon = longitude + ", " + latitude;
            printText(latlon);
        }catch(SecurityException e)
        {
            // TODO
        }
    }


    private String getUrl(double latitude , double longitude, String keywords, String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&keyword=" + keywords);
        googlePlaceUrl.append("&key="+"AIzaSyBfIXGXMiK1NIS6bxcVJxN6R-n_gZFfvTM");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyBfIXGXMiK1NIS6bxcVJxN6R-n_gZFfvTM");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
            buildGoogleApiClient();

            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }

    public void printText(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    public class HotelData extends AsyncTask<Object, String, String> {

        private String googlePlacesData;
        public List<HashMap<String, String>> nearbyPlaceList;

        @Override
        protected String doInBackground(Object... objects)
        {
            DownloadURL downloadURL = new DownloadURL();
            try
            {
                googlePlacesData = downloadURL.readUrl(url);
            }catch(IOException e)
            {
                // TODO
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String s){
            DataParser parser = new DataParser();
            nearbyPlaceList = parser.parse(s);
            showNearbyPlaces(nearbyPlaceList);
        }

        private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList)
        {
            googleHotels = nearbyPlaceList;
            nearbyHotels.clear();
            for(int i = 0; i < nearbyPlaceList.size(); i++)
            {
                HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                nearbyHotels.add(placeName);
            }
            mAdapter = new ArrayAdapter<String>
                    (context, android.R.layout.simple_list_item_1, nearbyHotels);
            mList.setAdapter(mAdapter);
        }
    }
}