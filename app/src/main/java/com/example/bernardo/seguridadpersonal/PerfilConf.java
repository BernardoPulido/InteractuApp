package com.example.bernardo.seguridadpersonal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bernardo.seguridadpersonal.Configuraciones.Principal_Configuracion;
import com.facebook.Profile;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import db.Alertas;
import db.MediosConexion;
import db.Parametros;

public class PerfilConf extends AppCompatActivity {

    public boolean banderaEdicion=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_perfil_conf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getFragmentManager().beginTransaction().replace(R.id.llenadoCardPerfil, new MyPreferenceFragment()).commit();
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilConf.this, MainActivity.class);
                intent.putExtra("comeperfil", 1);
                if(banderaEdicion){
                    intent.putExtra("perfil", 1);
                }else{
                    if(getIntent().getIntExtra("edito", 0)==1){
                        intent.putExtra("perfil", 1);
                    }
                }
                startActivity(intent);

            }
        });

    }

    private void CambiarPassTest(String actual, String nueva) {

        db.DateBaseHelper helperUpdatePerfil = new db.DateBaseHelper(PerfilConf.this);
        RuntimeExceptionDao<Parametros, Integer> daoPass = helperUpdatePerfil.getDaoREParametros();
        String ide="";
        try{
            Parametros id = daoPass.queryBuilder().where().eq("id", 3).queryForFirst();
            ide=id.getDescripcion();

        }catch (SQLException e){

        }

        final String passActual = actual;
        final String passNueva = nueva;
        final String idPAss = ide;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://mpcdemexico.com.mx/app/changePassword.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(PerfilConf.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PerfilConf.this,"¡Error! Revisa tu conexión a internet",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("actual", passActual);
                params.put("nueva", passNueva);
                params.put("id", idPAss);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }



    public class MyPreferenceFragment extends PreferenceFragment
    {
        EditTextPreference nombre;
        EditTextPreference apellidos;
        EditTextPreference edad;
        PreferenceScreen user;
        PreferenceScreen email;
        PreferenceScreen pass;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.edicion_perfil);

            nombre = (EditTextPreference)findPreference("name");
            apellidos = (EditTextPreference)findPreference("apellidos");
            edad = (EditTextPreference)findPreference("edad");
            user = (PreferenceScreen) findPreference("username");
            email = (PreferenceScreen) findPreference("email");
            pass = (PreferenceScreen) findPreference("pass");

            db.DateBaseHelper helper = new db.DateBaseHelper(PerfilConf.this);
            RuntimeExceptionDao<Parametros, Integer> daoPerfilFirst = helper.getDaoREParametros();

            try {
                Parametros NombreUsuario = daoPerfilFirst.queryBuilder().where().eq("id", 4).queryForFirst();
                nombre.setSummary(NombreUsuario.getDescripcion());
                nombre.setText(NombreUsuario.getDescripcion());

                Parametros ApellidosUsuario = daoPerfilFirst.queryBuilder().where().eq("id", 5).queryForFirst();
                apellidos.setSummary(ApellidosUsuario.getDescripcion());
                apellidos.setText(ApellidosUsuario.getDescripcion());

                Parametros EdadUsuario = daoPerfilFirst.queryBuilder().where().eq("id", 6).queryForFirst();
                if(EdadUsuario.getDescripcion().equals("0")){
                    edad.setSummary("23 años");
                    edad.setText("23");
                }else{
                    edad.setSummary(EdadUsuario.getDescripcion()+" años");
                    edad.setText(EdadUsuario.getDescripcion());
                }

                Parametros UsernameUsuario = daoPerfilFirst.queryBuilder().where().eq("id", 7).queryForFirst();
                if(UsernameUsuario.getDescripcion().contains("http")){
                    user.setSummary("Usuario Facebook");
                }else{
                    user.setSummary(UsernameUsuario.getDescripcion());
                }

                Parametros EmailUsuario = daoPerfilFirst.queryBuilder().where().eq("id", 2).queryForFirst();
                email.setSummary(EmailUsuario.getDescripcion());



            } catch (SQLException e) {
                e.printStackTrace();
            }
            helper.close();

            if(Profile.getCurrentProfile()!=null){
                pass.setEnabled(false);
            }

            nombre.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    nombre.setSummary(newValue.toString());
                    db.DateBaseHelper helperName = new db.DateBaseHelper(PerfilConf.this);
                    RuntimeExceptionDao<Parametros, Integer> NombreDao = helperName.getDaoREParametros();
                    try {
                        Parametros NombreEdit = NombreDao.queryBuilder().where().eq("id", 4).queryForFirst();
                        if(NombreEdit!=null){
                            NombreEdit.setDescripcion(newValue.toString());
                            NombreDao.update(NombreEdit);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    helperName.close();

                    banderaEdicion=true;

                    return true;
                }
            });

            apellidos.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    apellidos.setSummary(newValue.toString());
                    db.DateBaseHelper helperAellido = new db.DateBaseHelper(PerfilConf.this);
                    RuntimeExceptionDao<Parametros, Integer> ApellidoDao = helperAellido.getDaoREParametros();
                    try {
                        Parametros apellidoEdit = ApellidoDao.queryBuilder().where().eq("id", 5).queryForFirst();
                        if(apellidoEdit!=null){
                            apellidoEdit.setDescripcion(newValue.toString());
                            ApellidoDao.update(apellidoEdit);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    helperAellido.close();
                    banderaEdicion=true;

                    return true;
                }
            });

            edad.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    edad.setSummary(newValue.toString()+" años");
                    db.DateBaseHelper helperEdad= new db.DateBaseHelper(PerfilConf.this);
                    RuntimeExceptionDao<Parametros, Integer> EdadDao = helperEdad.getDaoREParametros();
                    try {
                        Parametros edadEdit = EdadDao.queryBuilder().where().eq("id", 6).queryForFirst();
                        if(edadEdit!=null){
                            edadEdit.setDescripcion(newValue.toString());
                            EdadDao.update(edadEdit);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    helperEdad.close();

                    banderaEdicion=true;
                    return true;
                }
            });

            pass.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    cambiarContraseña();
                    return true;
                }

            });
        }

        private void cambiarContraseña() {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PerfilConf.this);
                LayoutInflater inflater = PerfilConf.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_change_pass, null);
                dialogBuilder.setView(dialogView);

                final EditText edt0 = (EditText) dialogView.findViewById(R.id.pass0);
                final EditText edt = (EditText) dialogView.findViewById(R.id.pass1);
                final EditText edt2 = (EditText) dialogView.findViewById(R.id.pass2);

            final TextInputLayout actualTI = (TextInputLayout)dialogView.findViewById(R.id.pass0ti);
            final TextInputLayout nuveaTI = (TextInputLayout)dialogView.findViewById(R.id.pass1ti);
            final TextInputLayout confirmacionTI = (TextInputLayout)dialogView.findViewById(R.id.pass2ti);

                dialogBuilder.setTitle("Cambiar contraseña");
            dialogBuilder.setCancelable(false);
                dialogBuilder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                AlertDialog b = dialogBuilder.create();
                b.show();
            b.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;

                    String edit = edt.getText().toString();
                    String edit2 = edt2.getText().toString();
                    String edit0 = edt0.getText().toString();

                    if(edit0.trim().equals("")){
                        actualTI.setError("Debes ingresar tu contraseña actual");
                    }else{
                        actualTI.setErrorEnabled(false);
                    }
                    if(edit.trim().equals("")){
                        nuveaTI.setError("Debes ingresar tu nueva contraseña");
                    }else{
                        nuveaTI.setErrorEnabled(false);
                    }
                    if(edit.equals(edit2) && !edit0.trim().equals("") && !edit.trim().equals("")){
                        CambiarPassTest(edit0, edit);
                        wantToCloseDialog=true;
                    }else{
                        confirmacionTI.setError("Las contraseñas no coinciden");
                    }

                    if(wantToCloseDialog){
                       Intent intent = new Intent(PerfilConf.this, PerfilConf.class);
                        if(banderaEdicion){
                            intent.putExtra("edito", 1);
                        }
                        startActivity(intent);
                    }

                }
            });

        }


    }
}
