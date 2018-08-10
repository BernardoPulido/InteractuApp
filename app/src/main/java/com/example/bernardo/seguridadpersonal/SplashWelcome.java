package com.example.bernardo.seguridadpersonal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.bernardo.seguridadpersonal.Configuraciones.NotificacionActivado;
import com.example.bernardo.seguridadpersonal.Configuraciones.customHandler;
import com.example.bernardo.seguridadpersonal.PantallaPrincipal.HowWork;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.pushbots.push.Pushbots;

import java.sql.SQLException;

import db.Parametros;


/**
 * Created by Bernardo on 04/04/2016.
 */
public class SplashWelcome extends AppCompatActivity {

        private static int SPLASH_SCREEN_TIME = 2000;
    private boolean bandera=false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            //PushBots
            Pushbots.sharedInstance().init(this);
            Pushbots.sharedInstance().setCustomHandler(customHandler.class);
            super.onCreate(savedInstanceState);

            //Cerramos notificación en caso de existir
            NotificacionActivado.cancel(this);
            setContentView(R.layout.activity_welcome);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().hide();


            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Profile profile;
                    profile = Profile.getCurrentProfile();

                    if(profile!=null){

                        Intent intent = new Intent(SplashWelcome.this, MainActivity.class);
                        intent.putExtra("inicial", 1);
                        startActivity(intent);
                    }else{
                        db.DateBaseHelper helperUser = new db.DateBaseHelper(SplashWelcome.this);
                        RuntimeExceptionDao<Parametros, Integer> daoParametros = helperUser.getDaoREParametros();
                        try {
                            Parametros idUsuario = daoParametros.queryBuilder().where().eq("id", 3).queryForFirst();
                            if(idUsuario!=null && !idUsuario.getDescripcion().equals("")){
                                bandera=true;
                            }
                            helperUser.close();
                        }catch (SQLException e){}

                        if(bandera){
                            Intent intent = new Intent(SplashWelcome.this, MainActivity.class);
                            intent.putExtra("inicial", 1);
                            startActivity(intent);
                        }else {

                            //Revisamos para mostrar tutorial en una primera instancia
                            SharedPreferences estatusTutorial = getSharedPreferences("tutorialEstatus", Context.MODE_PRIVATE);

                            if(estatusTutorial.getBoolean("tutofirsttime", false)){
                                Intent intent = new Intent(SplashWelcome.this, Principal.class);
                                startActivity(intent);

                            }else {
                                SharedPreferences.Editor ed = estatusTutorial.edit();
                                ed.putBoolean("tutofirsttime", true);
                                ed.commit();
                                Intent intTuto = new Intent(SplashWelcome.this, HowWork.class);
                                intTuto.putExtra("comeTuto", 1);
                                startActivity(intTuto);

                            }
                        }
                    }


                    finish();
                }
            }, SPLASH_SCREEN_TIME);
        }
    }


