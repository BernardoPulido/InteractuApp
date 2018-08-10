package com.example.bernardo.seguridadpersonal.PantallaPrincipal;

/**
 * Created by luis_ on 27/07/2016.
 */

import android.content.ClipData;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.example.bernardo.seguridadpersonal.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AmigoVigilante extends android.app.Fragment {
    ListView lista;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView =inflater.inflate(R.layout.fragment_amigovigilante, container, false);
            lista = (ListView) rootView.findViewById(R.id.listaAmigos);
            final List<String[]> colorlist = new LinkedList<String[]>();
            db.DateBaseHelper helper = new db.DateBaseHelper(getActivity().getBaseContext());
            RuntimeExceptionDao<db.AmigoVigilante, Integer> daoAmigoVigilante = helper.getDaoREamigo();
            List<db.AmigoVigilante> amigos = daoAmigoVigilante.queryForAll();
            if(amigos.size()==0){
                Toast.makeText(getActivity(), "No cuentas con amigos vigilantes registrados.", Toast.LENGTH_SHORT).show();
            }
            for(int i=0; i<amigos.size(); i++){
                String numero = String.format("%06d",amigos.get(i).getAmigoRecepetor());
                if(amigos.get(i).getEstatus()==1){
                    colorlist.add(new String[]{"ID: "+numero, amigos.get(i).getCorreoAmigoReceptor(), "1"});
                }else{
                    colorlist.add(new String[]{"ID: "+numero, amigos.get(i).getCorreoAmigoReceptor(), "0"});
                }

            }

            ArrayAdapter<String[]> adapter = new ArrayAdapter<String[]>(getActivity().getBaseContext(), android.R.layout.simple_list_item_2, android.R.id.text1, colorlist){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    String[] entry = colorlist.get(position);

                    TextView text1 = (TextView)view.findViewById(android.R.id.text1);
                    text1.setTextAppearance(getActivity(), R.style.myestulo1);

                    TextView text2 = (TextView)view.findViewById(android.R.id.text2);
                    text2.setTextAppearance(getActivity(), R.style.myestulo);
                    if(entry[2].equals("1")){
                        text1.setEnabled(false);
                        text1.setTextColor(getResources().getColor(R.color.silver2));
                        text2.setEnabled(false);
                    }else{
                        text1.setEnabled(true);
                        text2.setEnabled(true);
                    }
                    text1.setText(entry[0]);
                    text2.setText(entry[1]);
                    return view;
                }
            };

            lista.setAdapter(adapter);
            registerForContextMenu(lista);

            return rootView;

        }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_litsa, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String key = ((TextView)(info.targetView.findViewById(android.R.id.text1))).getText().toString();
        String[] content = key.split(" ");

        db.DateBaseHelper helper = new db.DateBaseHelper(getActivity().getBaseContext());
        RuntimeExceptionDao<db.AmigoVigilante, Integer> daoAmigoVigilante = helper.getDaoREamigo();
        switch (item.getItemId()) {
            case R.id.Eliminar:
                try {
                    List<db.AmigoVigilante> eliminar = daoAmigoVigilante.queryBuilder().where().eq("amigoRecepetor",Integer.parseInt(content[content.length-1])).query();
                    daoAmigoVigilante.delete(eliminar);
                    android.app.FragmentManager fm = getFragmentManager();
                    android.app.FragmentTransaction ft = fm.beginTransaction();

                    AmigoVigilante tff = new AmigoVigilante();
                    ft.replace(R.id.IntentoMain, tff, "SETTING");
                    ft.commit();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Log.d("ID", Integer.parseInt(content[content.length-1])+"");
                return true;
            case R.id.desactivar:
                try {
                    db.AmigoVigilante eliminar = daoAmigoVigilante.queryBuilder().where().eq("amigoRecepetor",Integer.parseInt(content[content.length-1])).queryForFirst();

                    if(eliminar.getEstatus()==1){
                        eliminar.setEstatus(0);
                    }else{
                        eliminar.setEstatus(1);
                    }

                    daoAmigoVigilante.update(eliminar);

                    android.app.FragmentManager fm = getFragmentManager();
                    android.app.FragmentTransaction ft = fm.beginTransaction();

                    AmigoVigilante tff = new AmigoVigilante();
                    ft.replace(R.id.IntentoMain, tff, "SETTING");
                    ft.commit();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
