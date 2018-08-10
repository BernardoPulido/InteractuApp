package com.example.bernardo.seguridadpersonal.PantallaPrincipal;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bernardo.seguridadpersonal.R;

/**
 * Created by Bernardo on 18/05/2016.
 */
public class Tutorial extends android.app.Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);


        return inflater.inflate(R.layout.fragent_tutorial, container, false);
    }
}
