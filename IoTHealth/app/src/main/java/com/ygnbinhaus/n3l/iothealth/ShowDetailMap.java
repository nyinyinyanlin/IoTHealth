package com.ygnbinhaus.n3l.iothealth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowDetailMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(100);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = getIntent();
        TextView date = (TextView) findViewById(R.id.dateBs);
        TextView heartrate = (TextView) findViewById(R.id.heartrateBs);
        TextView oxygen = (TextView) findViewById(R.id.oxygenBs);
        TextView temperature = (TextView) findViewById(R.id.temperatureBs);
        TextView humidity = (TextView) findViewById(R.id.humidityBs);
        TextView location = (TextView) findViewById(R.id.locationBs);
        TextView comment = (TextView) findViewById(R.id.commentBs);
        date.setText(intent.getStringExtra("EXTRA_DATE"));
        heartrate.setText(intent.getStringExtra("EXTRA_HEARTRATE"));
        oxygen.setText(intent.getStringExtra("EXTRA_OXYGEN"));
        temperature.setText(intent.getStringExtra("EXTRA_TEMPERATURE"));
        humidity.setText(intent.getStringExtra("EXTRA_HUMIDITY"));
        location.setText(intent.getStringExtra("EXTRA_LATITUDE")+" : "+intent.getStringExtra("EXTRA_LONGITUDE"));
        if(!intent.getStringExtra("EXTRA_COMMENT").equals("")){
            comment.setText(intent.getStringExtra("EXTRA_COMMENT"));
            comment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng userLocation = new LatLng(Double.parseDouble(getIntent().getStringExtra("EXTRA_LATITUDE")), Double.parseDouble(getIntent().getStringExtra("EXTRA_LONGITUDE")));
        mMap.addMarker(new MarkerOptions().position(userLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,18));
    }
}
