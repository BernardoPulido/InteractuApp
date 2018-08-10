package com.example.bernardo.seguridadpersonal.PantallaPrincipal;

/**
 * Created by Bernardo on 05/09/2016.
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bernardo.seguridadpersonal.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.Collections;
import java.util.List;

import db.HistorialAlertas;


public class Historial extends android.app.Fragment{

    ListView list;
    String[] itemname;

    Integer[] imgid;
    String[] medios;
    String[] latitudes;
    String[] longitudes;
    String[] complemento;
    String[] mensajes;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView =inflater.inflate(R.layout.historial_layout, container, false);

        db.DateBaseHelper helperHistorial = new db.DateBaseHelper(getActivity());
        RuntimeExceptionDao<HistorialAlertas, Integer> daoHistorial = helperHistorial.getDaoREhistorial();
        //daoHistorial.delete(daoHistorial.queryForAll());
        List<HistorialAlertas> listaHistoial = daoHistorial.queryForAll();
        Collections.reverse(listaHistoial);

        if(listaHistoial.size()==0){
            Toast.makeText(getActivity(), "No hay registros.", Toast.LENGTH_SHORT).show();
        }

        itemname = new String[listaHistoial.size()];
        imgid= new Integer[listaHistoial.size()];
        medios= new String[listaHistoial.size()];
        complemento= new String[listaHistoial.size()];
        latitudes= new String[listaHistoial.size()];
        longitudes= new String[listaHistoial.size()];
        mensajes= new String[listaHistoial.size()];
        int contador=0;
        for (HistorialAlertas history:listaHistoial) {
            //daoHistorial.delete(history);
            itemname[contador]=history.getFecha();
            if(history.getIdUsuario()==0){
                imgid[contador]=R.drawable.ic_call_made_black_48dp;
            }else{
                imgid[contador]=R.drawable.ic_call_received_black_48dp;
            }
            medios[contador]=history.getMedioConexion();
            String[] arregloaux = history.getPosicionGPS().split(", ");
            complemento[contador] = arregloaux[0];
            latitudes[contador] = arregloaux[1];
            longitudes[contador] = arregloaux[2];
            mensajes[contador] = history.getMensaje();

            contador++;
        }


        CustomListAdapter adapter=new CustomListAdapter(getActivity(), itemname, imgid, medios, complemento, mensajes);
        list=(ListView)rootView.findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("Posicion", position+"");
                String Slecteditem= itemname[+position];

                  Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("lon", longitudes[position]);
                intent.putExtra("lat", latitudes[position]);
                intent.putExtra("user", medios[position]);
                intent.putExtra("msg", mensajes[position]);
                intent.putExtra("history", 1);
                startActivity(intent);


                Log.d("Item:", Slecteditem);

            }
        });

        return rootView;

    }

}
