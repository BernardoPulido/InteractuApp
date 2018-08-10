package com.example.bernardo.seguridadpersonal;

/**
 * Created by Bernardo on 05/04/2016.
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.bernardo.seguridadpersonal.Configuraciones.customHandler;
import com.example.bernardo.seguridadpersonal.Registro.Registro;
import com.pushbots.push.Pushbots;


@SuppressLint("NewApi")
public class Principal extends Activity implements OnClickListener {

    Button boton1, boton2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        //getActionBar().hide();
        boton1 = (Button)findViewById(R.id.info);
        boton2 = (Button)findViewById(R.id.informacionAviso);
        boton1.setOnClickListener(this);
        boton2.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.info:
                Intent i = new Intent(this, PruebaDeLogueo.class);
                startActivity(i);

                break;
            case R.id.informacionAviso:
                Intent n = new Intent(this, Registro.class);
                startActivity(n);
                break;
        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }
}

