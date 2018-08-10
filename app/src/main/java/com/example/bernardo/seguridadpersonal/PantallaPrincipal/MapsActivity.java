package com.example.bernardo.seguridadpersonal.PantallaPrincipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


import com.example.bernardo.seguridadpersonal.Configuraciones.NotificacionActivado;
import com.example.bernardo.seguridadpersonal.MainActivity;
import com.example.bernardo.seguridadpersonal.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import db.HistorialAlertas;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double latitud;
    String user;
    String msg;
    double longitud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mapa");
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                if(getIntent().getIntExtra("history",0)==1){
                    intent.putExtra("history", 1);
                }
                startActivity(intent);

            }
        });


        //Cerramos notificación en caso de existir
        NotificacionActivado.cancel(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle extras = getIntent().getExtras();


        latitud = Double.parseDouble(extras.getString("lat"));
        longitud = Double.parseDouble(extras.getString("lon"));
        user = extras.getString("user");
        msg = extras.getString("msg");

        if(getIntent().getIntExtra("comepush", 0)==1){
            //Guardar registro en historial
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String formattedDate = df.format(c.getTime());
            db.DateBaseHelper helperHistorial = new db.DateBaseHelper(MapsActivity.this);
            RuntimeExceptionDao<HistorialAlertas, Integer> daoHistorial = helperHistorial.getDaoREhistorial();
            daoHistorial.create(new HistorialAlertas("Mecanismo amigo vigilante", "", msg, user+", "+latitud+", "+longitud, formattedDate+"", "", 1));
            helperHistorial.close();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng position = new LatLng(latitud, longitud);

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(position)
                .zoom(15)
                .build();
        Marker marker =mMap.addMarker(new MarkerOptions().position(position).title(user).snippet(msg)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        marker.showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
