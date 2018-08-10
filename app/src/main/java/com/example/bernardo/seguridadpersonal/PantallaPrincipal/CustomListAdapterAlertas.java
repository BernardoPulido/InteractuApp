package com.example.bernardo.seguridadpersonal.PantallaPrincipal;

/**
 * Created by Bernardo on 30/09/2016.
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

public class CustomListAdapterAlertas extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] mensajes;
    private final Integer[] ids;
    private final Integer[] imgid;

    public CustomListAdapterAlertas(Activity context, String[] itemname, Integer[] ids,String[] mensajes, Integer[] imgid) {
        super(context, R.layout.alertaslist, itemname);
        // TODO Auto-generated constructor stub


        this.context=context;
        this.itemname=itemname;
        this.ids=ids;
        this.mensajes = mensajes;
        this.imgid=imgid;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.alertaslist, null,true);

        TextView titulo = (TextView) rowView.findViewById(R.id.manualnombre);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgmanuales);
        TextView mensaje = (TextView) rowView.findViewById(R.id.manualmensaje);

        titulo.setText(itemname[position]);
        mensaje.setText(mensajes[position]);

        Resources res = parent.getContext().getResources();
        Drawable drawable = res.getDrawable(imgid[position]);
        drawable = DrawableCompat.wrap(drawable);

        DrawableCompat.setTint(drawable, parent.getContext().getResources().getColor(R.color.colorAccent));
        imageView.setImageResource(imgid[position]);


        return rowView;

    };
}
