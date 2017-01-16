package com.example.justynaa.kampusaghmapa;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private LatLng znacznikStart;
    private LatLng znacznikKoniec;
    private double[][] punktyLinii;

    Button button;
    EditText start;
    EditText finish;
    int s=0,f=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        button=(Button)findViewById(R.id.button);
        start=(EditText)findViewById(R.id.start);
        finish=(EditText)findViewById(R.id.koniec);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendRequest();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendRequest() throws IOException {
        String origin = start.getText().toString();
        String destination = finish.getText().toString();
        /*String s = start.getText().toString();
        String k = finish.getText().toString();
        int origin = Integer.valueOf(String.valueOf(start.getText()));
        int destination = Integer.valueOf(String.valueOf(start.getText()));*/

        if (origin.isEmpty()) {
            Toast.makeText(this, "Podaj miejsce z którego chcesz wystartować", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Podaj miejsce do którego chcesz dojść", Toast.LENGTH_SHORT).show();
            return;
        }

        /*try {
            s= Integer.parseInt(start.getText().toString());  // przydatne
            f= Integer.parseInt(finish.getText().toString());
            //Toast.makeText(this, "Odzcytano miejsca " +s+ " oraz "+ f,Toast.LENGTH_SHORT).show();
        } catch(NumberFormatException nfe) {
            Toast.makeText(this, "Could not parse " + nfe,Toast.LENGTH_SHORT).show();
        }*/
       // Toast.makeText(this, "Odzcytano miejsca " +origin+ " oraz "+ destination,Toast.LENGTH_SHORT).show();
        new DirectionFinder(this,this,origin,destination).execute();
       /* try {

            new DirectionFinder(this,this,origin,destination).execute();
            //new DirectionFinder(this,this,s,f).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    /*@RequiresApi(api = Build.VERSION_CODES.M)
    private Boolean permissionsGranted() {
        return getBaseContext().checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }*/


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
        LatLng informatyka = new LatLng(50.067994, 19.912408);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(informatyka, 18));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.znak))
                .title("Informatyka")
                .position(informatyka)));
        //LatLng fizyka = new LatLng(50.066854, 19.913087);

        /*mMap.addMarker(new MarkerOptions().position(informatyka).title("Wydział Informatyki"));
        mMap.addMarker(new MarkerOptions().position(fizyka).title("Wydział Fizyki"));
        mMap.addPolyline(new PolylineOptions().add(
                informatyka,
                new LatLng(50.068077, 19.912833),
                new LatLng(50.067867, 19.913326),
                new LatLng(50.067619, 19.913256),
                new LatLng(50.067530, 19.913809),
                new LatLng(50.066775, 19.913411),
                fizyka
            )
        );*/


        // Add a marker in Sydney and move the camera

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(informatyka, 18));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onDirectionFinderStart() {
        mMap.clear();
        //  nadpisana metoda z interfejsu DirectionFinderlistener służaca usuwaniu punktow i linii po poprzednim wyszukiwaniu

       /*if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }*/
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        //progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "Odzcytano miejsca ",Toast.LENGTH_SHORT).show();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        Route p = routes.get(0);
        Route k = routes.get(routes.size()-1);

        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.znakstart))
                .title("poczatek")
                .position(p.startKordy)));
        destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.znakkoniec))
                .title("koniec")
                .position(k.koniecKordy)));


        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startKordy, 16));
            ((TextView) findViewById(R.id.czas)).setText(""+route.dystans);
            ((TextView) findViewById(R.id.dystans)).setText(""+route.dystans);

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.RED).
                    width(10);

            mMap.addPolyline(polylineOptions.add(
                    route.startKordy,
                    route.koniecKordy
            ));
            /*PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.punkty.size(); i++)
                polylineOptions.add(route.punkty.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));*/
        }
    }
}













