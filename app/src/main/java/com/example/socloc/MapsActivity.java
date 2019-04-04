package com.example.socloc;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.annotation.NonNull;
import android.content.pm.PackageManager;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.app.Activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationCallback;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng latLng;
    private MarkerOptions markerOptions;
    private Marker marker;
    private Button localizeButton;
    protected LocationCallback locationCallback;
    protected Location userLocation;
    private FusedLocationProviderClient fusedLocationClient;

    private static final String TAG = "MapsActivity";
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.util.Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        allowLocationUpdates();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        userLocation = location;
                        moveMapToLocation();
                        Log.i(TAG, " Location: " + userLocation);
                    }
                }
            }
        };

        markerOptions = new MarkerOptions();
        this.localizeButton = this.findViewById(R.id.localize);
        this.localizeButton.setOnClickListener((View view) -> {
            android.util.Log.i(TAG, "Clicked!");
            try {
                fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) view.getContext(), location -> {
                    if (location != null) {
                        userLocation = location;
                        moveMapToLocation();
                    }
                });
                Log.i(TAG, " Location: " + userLocation);
            } catch (SecurityException se) {
                Log.i(TAG, se.getMessage());
            }
        });

    }

    private void allowLocationUpdates() {
        int locationRequestCode = 1000;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                locationRequestCode
            );
        } else {
            Log.i(TAG, "PERMISSION GRANTED!");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        android.util.Log.i(TAG, "onMapReady()");
        final LatLng PERTH = new LatLng(-11.90, 115.86);
        marker = mMap.addMarker(
            markerOptions.position(PERTH)
            .draggable(true)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        android.util.Log.i(TAG, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        android.util.Log.i(TAG, "onStop()");
    }

    private void moveMapToLocation() {
        latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        markerOptions.position(latLng);
        marker.setPosition(latLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                        if (location != null) {
                            userLocation = location;
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

}
