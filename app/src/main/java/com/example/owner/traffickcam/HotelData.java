package com.example.owner.traffickcam;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HotelData extends AsyncTask<Object, String, String>{

    private String googlePlacesData;
    private ListView mList;
    String url;
    public List<HashMap<String, String>> nearbyPlaceList;

    @Override
    protected String doInBackground(Object... objects)
    {
        mList = (ListView)objects[0];
        url = (String)objects[1];

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
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lng"));

            LatLng latLng = new LatLng( lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : "+ vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        }
    }
}