package com.example.bernardo.seguridadpersonal.PantallaPrincipal;

/**
 * Created by Bernardo on 05/09/2016.
 */
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bernardo.seguridadpersonal.R;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] complemento;
    private final String[] medios;
    private final String[] mensajes;
    private final Integer[] imgid;

    public CustomListAdapter(Activity context, String[] itemname, Integer[] imgid,String[] medios, String[] complemento, String[] mensajes) {
        super(context, R.layout.historialist, itemname);
        // TODO Auto-generated constructor stub

        this.medios = medios;
        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.complemento = complemento;
        this.mensajes = mensajes;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.historialist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
        TextView tvnew = (TextView) rowView.findViewById(R.id.tvnew);

        txtTitle.setText(itemname[position]);
        Resources res = parent.getContext().getResources();
        Drawable drawable = res.getDrawable(imgid[position]);
        drawable = DrawableCompat.wrap(drawable);
        if(imgid[position]==R.drawable.ic_call_made_black_48dp){
            DrawableCompat.setTint(drawable, parent.getContext().getResources().getColor(android.R.color.holo_green_dark));
            //imageView.setImageResource(imgid[position]);
        }else{
            DrawableCompat.setTint(drawable, parent.getContext().getResources().getColor(android.R.color.holo_red_dark));
            //imageView.setImageResource(imgid[position]);
        }

        extratxt.setText(medios[position]);
        tvnew.setText(mensajes[position]);


        return rowView;

    };
}
