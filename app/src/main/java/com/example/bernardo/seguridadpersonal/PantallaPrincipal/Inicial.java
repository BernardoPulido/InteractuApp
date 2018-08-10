package com.example.bernardo.seguridadpersonal.PantallaPrincipal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Bernardo on 18/05/2016.
 */
public class Inicial extends Fragment {


    final static String LAYOUT_ID = "layoutId";

    public static Inicial newInstance(int layoutId) {
        Inicial pane = new Inicial();
        Bundle bundle = new Bundle();
        bundle.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(bundle);
        return pane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(getArguments().getInt(LAYOUT_ID, -1),
                container, false);

        return root;
    }

}
