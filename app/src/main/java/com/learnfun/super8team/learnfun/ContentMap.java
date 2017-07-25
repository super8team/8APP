package com.learnfun.super8team.learnfun;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static android.view.Window.FEATURE_NO_TITLE;

public class ContentMap extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private final int CAM_MOVED = 0;
    private int cam = 0;
    private Location mygps = null;
    private ArrayList<Location> locations;
    private String[] names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_content_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        locations = (ArrayList<Location>) intent.getSerializableExtra("locations");
        names = intent.getStringArrayExtra("names");
//        mygps = new Location("mygps");
//        mygps.setLatitude(127.267);
//        mygps.setLongitude(37.413);
        //로케이션값들 받아옴

        setResult(5229);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // only check
            return ;
        }

        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    20,
                    0,
                    this);
        } // end if network envables

        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    20,
                    0,
                    this);
        } // end if gps enabled
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

        mMap.clear();
        // Add a marker in Sydney and move the camera
        if(mygps != null){
            LatLng Me = new LatLng(mygps.getLatitude(), mygps.getLongitude());
            mMap.addMarker(new MarkerOptions().position(Me).title("Marker in Me"));

            if(cam == CAM_MOVED){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Me,16));
                cam++;
            }

        }


        //컨텐츠 갯수많큼 위치생성
        for(int i=0;i<locations.size();i++){
            LatLng contents = new LatLng(locations.get(i).getLatitude(),locations.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(contents).title("Marker in "+names[i]));
            //각 컨텐츠위치를 저장후 마킹
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        //현재 위치값을 멤버 변수에 저장
        mygps = location;
        onMapReady(mMap);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
