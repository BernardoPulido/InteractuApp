package com.example.bernardo.seguridadpersonal.Configuraciones;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.bernardo.seguridadpersonal.AlertasConf;
import com.example.bernardo.seguridadpersonal.MainActivity;
import com.example.bernardo.seguridadpersonal.Principal;
import com.example.bernardo.seguridadpersonal.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import db.Alertas;

/**
 * Created by Bernardo on 18/04/2016.
 */
public class Principal_Configuracion extends PreferenceFragment{
    SwitchPreference checkGPS;

    private static final int PERMISSIONS_REQUEST_PHONE_GPS = 99;
    final int REQUEST_LOCATION=1;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.principalconfiguracion);

        db.DateBaseHelper helper = new db.DateBaseHelper(getActivity().getBaseContext());
        RuntimeExceptionDao<Alertas, Integer> daoAlerta = helper.getDaoREalertas();

        List<Alertas> lista = daoAlerta.queryForAll();

        checkGPS = (SwitchPreference)findPreference("permitirGPS");

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            checkGPS.setChecked(false);
            if(lista.size()==0){

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Es necesario otorgar los permisos de ubicación.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }

        }

        //Switch de permisos para GPS
        checkGPS.
                setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("true")) {
                            //Inicio
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {

                                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                                    // Aquí muestras confirmación explicativa al usuario
                                    // por si rechazó los permisos anteriormente
                                } else {
                                    ActivityCompat.requestPermissions(
                                            getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQUEST_LOCATION);
                                }
                            }
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }
}

