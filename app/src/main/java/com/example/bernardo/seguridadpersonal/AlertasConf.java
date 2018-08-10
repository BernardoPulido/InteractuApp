package com.example.bernardo.seguridadpersonal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bernardo.seguridadpersonal.Configuraciones.ChipsMultiAutoCompleteTextview;
import com.example.bernardo.seguridadpersonal.Configuraciones.Reconocimiento;
import com.facebook.Profile;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.ArrayList;

import db.Alertas;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.app.Activity;
import android.os.StrictMode;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import db.MediosConexion;
import db.Parametros;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class AlertasConf extends AppCompatActivity {

    private int idRecibidoAlerta =0;
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    public static final int WEBVIEW_REQUEST_CODE = 100;
    private static final int PERMISSIONS_REQUEST_PHONE_CALL = 99;
    private static String[] PERMISSIONS_PHONECALL = {Manifest.permission.CALL_PHONE};

    private ProgressDialog pDialog;

    private static Twitter twitter;
    private static RequestToken requestToken;

    private static SharedPreferences sharedPreferences;

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTwitterConfigs();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if(TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
            Toast.makeText(this, "Twitter key or secret not configured",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        sharedPreferences = getSharedPreferences(PREF_NAME, 0);

        boolean isLoggedIn = sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
        Uri uri = getIntent().getData();

        if(uri != null && uri.toString().startsWith(callbackUrl)) {
            String verifier = uri.getQueryParameter(oAuthVerifier);
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                long userId = accessToken.getUserId();
                final User user = twitter.showUser(userId);
                final String username = user.getName();

                saveTwitterInfo(accessToken);
/*
                loginLayout.setVisibility(View.GONE);
                shareLayout.setVisibility(View.VISIBLE);
                userName.setText(getString(R.string.hello) + " " + username);*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Recuperar Id de alerta
        this.idRecibidoAlerta = getIntent().getIntExtra("id", 0);
        Log.d("Id Alerta", idRecibidoAlerta+"");
        setContentView(R.layout.activity_alertas_conf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getFragmentManager().beginTransaction().replace(R.id.llenadoCard1, new FragmentoAuxiliar()).commit();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlertasConf.this, MainActivity.class);
                intent.putExtra("confAlertas", 1);
                startActivity(intent);
            }
        });

    }

    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }

    private void saveTwitterInfo(AccessToken accessToken) {

        long userId = accessToken.getUserId();

        User user;

        try {

            user = twitter.showUser(userId);
            String username = user.getName();

            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            e.putString(PREF_USER_NAME, username);
            e.commit();

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    private void loginToTwitter() {

        boolean isLoggedIn = sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);

        if(!isLoggedIn) {
            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);

            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter.getOAuthRequestToken(callbackUrl);

                final Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                startActivityForResult(intent, WEBVIEW_REQUEST_CODE);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else {
            // loginLayout.setVisibility(View.GONE);
            //shareLayout.setVisibility(View.VISIBLE);
            //Toast.makeText(this,"Ya estas logueafo", Toast.LENGTH_LONG).show();
            Log.d("Estatus", "Ya estas logueado");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            String verifier = data.getExtras().getString(oAuthVerifier);

            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                long userId = accessToken.getUserId();
                final User user = twitter.showUser(userId);
                String username = user.getName();

                saveTwitterInfo(accessToken);


            } catch (Exception e) {
                e.printStackTrace();
                //Aqui eliminar todos los medios que tengan Twitter
                db.DateBaseHelper helperDeleteMedios = new db.DateBaseHelper(AlertasConf.this);
                RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperDeleteMedios.getDaoREmedios();
                try{
                    List<MediosConexion> listaAELiminar = daoMedios.queryBuilder().where().eq("medio", 2).query();
                    daoMedios.delete(listaAELiminar);
                }catch (SQLException ex){}

                helperDeleteMedios.close();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public class FragmentoAuxiliar extends PreferenceFragment
    {

        private static final int REQUEST_CODE = 1234;

        ChipsMultiAutoCompleteTextview mu;
        ChipsMultiAutoCompleteTextview mu2;
        AlertDialog.Builder alertDialog;
        AlertDialog.Builder alertDialog2;
        Dialog match_text_dialog;
        ListView textlist;
        ArrayList<String> matches_text;
        Preference alertaVoz;
        Preference checkTwitter;
        Preference checkTelegram;
        Preference checkAmigosVigilantes;
        Preference checkFB;
        Preference checkLL;
        EditTextPreference numero;


        boolean actionFB=false;
        boolean actionTW=false;
        boolean actionTG=false;
        boolean actionAV=false;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.edicion_alertas);
            alertaVoz = (Preference)findPreference("iniciardialogo");
            alertaVoz.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (isConnected()) {
                        stopService(new Intent(AlertasConf.this, Reconocimiento.class));
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        startActivityForResult(intent, REQUEST_CODE);
                    } else {
                        Toast.makeText(getApplicationContext(), "No cuentas con una conexión a internet", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }

            });

           // PreferenceManager.setDefaultValues(getActivity(), R.xml.edicion_alertas, true);
            //Llamada de emergencia
            numero = (EditTextPreference)findPreference("numero");

            db.DateBaseHelper helperAuxMedio = new db.DateBaseHelper(AlertasConf.this);
            RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperAuxMedio.getDaoREmedios();
            try {
                MediosConexion alertaAux = daoMedios.queryBuilder().where().eq("idAlerta", idRecibidoAlerta).and().eq("medio", 4).queryForFirst();
                if(alertaAux!=null) {
                    numero.setText(alertaAux.getPersonas());
                    numero.setSummary(alertaAux.getPersonas());
                }
            }catch (SQLException e){}

            numero.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    numero.setSummary(newValue.toString());
                    if(newValue.toString().equals("")){
                        numero.setSummary("Ingresa un número telefónico a quien se llamará en caso de emergencia");

                        db.DateBaseHelper helperMedios = new db.DateBaseHelper(AlertasConf.this);
                        RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperMedios.getDaoREmedios();
                        List<MediosConexion> eliminarMedioLlamada = null;
                        try {
                            eliminarMedioLlamada = daoMedios.queryBuilder().where().eq("idAlerta",idRecibidoAlerta).and().eq("medio", 4).query();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        daoMedios.delete(eliminarMedioLlamada);
                        helperMedios.close();
                    }else{
                        db.DateBaseHelper helperMedios = new db.DateBaseHelper(AlertasConf.this);
                        RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperMedios.getDaoREmedios();
                        try {
                            MediosConexion revisiion =  daoMedios.queryBuilder().where().eq("idAlerta",idRecibidoAlerta).and().eq("medio", 4).queryForFirst();
                            if(revisiion!=null){
                                revisiion.setPersonas(newValue.toString());
                                daoMedios.update(revisiion);
                            }else{
                                MediosConexion medio = new MediosConexion(4, newValue.toString(), idRecibidoAlerta);
                                daoMedios.create(medio);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        helperMedios.close();
                    }

                    return true;
                }
            });
            numero.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                   llamadaEmergencia();
                    return true;
                }

            });

            //Facebook
            checkFB = (Preference)findPreference("facebook");
            if(Profile.getCurrentProfile()==null){
                checkFB.setEnabled(false);
            }

            checkFB.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if (newValue.toString().equals("true")) {
                                db.DateBaseHelper helperMedios = new db.DateBaseHelper(AlertasConf.this);
                                RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperMedios.getDaoREmedios();
                                MediosConexion medio = new MediosConexion(1, "", idRecibidoAlerta);
                                daoMedios.create(medio);
                                helperMedios.close();

                            } else {
                                db.DateBaseHelper helperMedios = new db.DateBaseHelper(AlertasConf.this);
                                RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperMedios.getDaoREmedios();
                                List<MediosConexion> eliminarMedioFB = null;
                                try {
                                    eliminarMedioFB = daoMedios.queryBuilder().where().eq("idAlerta",idRecibidoAlerta).and().eq("medio", 1).query();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                daoMedios.delete(eliminarMedioFB);
                                helperMedios.close();
                            }
                            return true;
                        }
                    });

            //Twitter
            checkTwitter = (Preference)findPreference("twitter");
            checkTwitter.
                    setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if (newValue.toString().equals("true")) {
                                db.DateBaseHelper helperMedios = new db.DateBaseHelper(AlertasConf.this);
                                RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperMedios.getDaoREmedios();
                                MediosConexion medio = new MediosConexion(2, "", idRecibidoAlerta);
                                daoMedios.create(medio);
                                helperMedios.close();

                                loginToTwitter();

                            } else {
                                db.DateBaseHelper helperMedios = new db.DateBaseHelper(AlertasConf.this);
                                RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperMedios.getDaoREmedios();
                                List<MediosConexion> eliminarMedioTW = null;
                                try {
                                    eliminarMedioTW = daoMedios.queryBuilder().where().eq("idAlerta",idRecibidoAlerta).and().eq("medio", 2).query();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                daoMedios.delete(eliminarMedioTW);
                                helperMedios.close();
                            }
                            return true;
                        }
                    });


            checkTelegram = (Preference)findPreference("telegram");
            final PreferenceScreen personasTelegram = (PreferenceScreen)findPreference("contactosTelegram");

            db.DateBaseHelper helpertelegra = new db.DateBaseHelper(AlertasConf.this);
            RuntimeExceptionDao<Parametros, Integer> daoParametros = helpertelegra.getDaoREParametros();
            try {
                List<Parametros> busquedaParaSummary = daoParametros.queryBuilder().where().eq("id",1).query();
                if(busquedaParaSummary.size()>0){
                    checkTelegram.setSummary("Tu ID Telegram: "+busquedaParaSummary.get(0).getDescripcion().toString());

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            helpertelegra.close();
            checkTelegram.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {



                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if (newValue.toString().equals("true")) {
                                db.DateBaseHelper helperMedios = new db.DateBaseHelper(AlertasConf.this);

                                RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperMedios.getDaoREmedios();
                                MediosConexion medio = new MediosConexion(3, "", idRecibidoAlerta);
                                daoMedios.create(medio);
                                Log.d("Registros", daoMedios.queryForAll().size()+"");
                                helperMedios.close();
                                personasTelegram.setEnabled(true);
                                if(checkTelegram.getSummary().toString().equals("Alertar por este medio")){

                                    PackageManager pm = AlertasConf.this.getPackageManager();
                                    try {
                                        pm.getPackageInfo("org.telegram.messenger", PackageManager.GET_ACTIVITIES);
                                        Intent telegram = new Intent(android.content.Intent.ACTION_VIEW);
                                        telegram.setData(Uri.parse("http://telegram.me/SeguridadPersonalBot"));
                                        telegram.setPackage("org.telegram.messenger");
                                        startActivity(telegram);
                                        //consultarEstatusConTelegram();
                                        Toast.makeText(getApplicationContext(), "Debes iniciar con BotSeguridadPersonal",
                                                Toast.LENGTH_SHORT).show();

                                    } catch (PackageManager.NameNotFoundException e) {
                                        Toast.makeText(AlertasConf.this, "No cuentas con la aplicación de Telegram.", Toast.LENGTH_SHORT).show();
                                        personasTelegram.setEnabled(false);

                                        db.DateBaseHelper helperMediosdos = new db.DateBaseHelper(AlertasConf.this);

                                        RuntimeExceptionDao<MediosConexion, Integer> daoMediosdos = helperMediosdos.getDaoREmedios();
                                        List<MediosConexion> eliminarMedioTelegramdos = null;
                                        try {
                                            eliminarMedioTelegramdos = daoMediosdos.queryBuilder().where().eq("idAlerta",idRecibidoAlerta).and().eq("medio", 3).query();
                                        } catch (SQLException edos) {
                                            edos.printStackTrace();
                                        }
                                        daoMediosdos.delete(eliminarMedioTelegramdos);
                                        helperMediosdos.close();
                                    }

                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AlertasConf.this);
                                    builder.setMessage(checkTelegram.getSummary().toString())
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //Solo es informativo
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }


                            } else {
                                db.DateBaseHelper helperMedios = new db.DateBaseHelper(AlertasConf.this);

                                RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperMedios.getDaoREmedios();
                               List<MediosConexion> eliminarMedioTelegram = null;
                                try {
                                    eliminarMedioTelegram = daoMedios.queryBuilder().where().eq("idAlerta",idRecibidoAlerta).and().eq("medio", 3).query();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                daoMedios.delete(eliminarMedioTelegram);

                                Log.d("Registros", daoMedios.queryForAll().size()+"");
                                helperMedios.close();
                                personasTelegram.setEnabled(false);
                              /*  Toast.makeText(getApplicationContext(), "CB: " + "false",
                                        Toast.LENGTH_SHORT).show();*/
                            }


                            return true;
                        }
                    });

            //Contactos a alertar por Telegram



            personasTelegram.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    consultarEstatusConTelegram();

                    return false;

                }
            });




            //Eliminar alerta
            Preference eliminarAlerta = (Preference)findPreference("eliminar");
            eliminarAlerta.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    db.DateBaseHelper helper = new db.DateBaseHelper(AlertasConf.this);
                    RuntimeExceptionDao<Alertas, Integer> daoAlerta = helper.getDaoREalertas();
                    Alertas a = daoAlerta.queryForId(idRecibidoAlerta);
                    daoAlerta.deleteById(a.getIdAlerta());
                    helper.close();
                    stopService(new Intent(AlertasConf.this, Reconocimiento.class));
                    Intent intentillo = new Intent(AlertasConf.this, Reconocimiento.class);
                    intentillo.putExtra("bandera", 1); //Con esta bandera el servicio debe actualizar la lista de frases almacenadas
                    startService(intentillo);

                    Intent intent = new Intent(AlertasConf.this, MainActivity.class);
                    startActivity(intent);

                    return false;
                }

            });


            //Mensaje de alerta
            final EditTextPreference prefe = (EditTextPreference)findPreference("mensaje");
            final EditTextPreference pref = (EditTextPreference)findPreference("nombrealerta");
            db.DateBaseHelper helper = new db.DateBaseHelper(AlertasConf.this);
            RuntimeExceptionDao<Alertas, Integer> daoAlerta = helper.getDaoREalertas();
            Alertas a = daoAlerta.queryForId(idRecibidoAlerta);
            if(a!=null){
                prefe.setSummary(a.getMensaje().toString());
                prefe.setText(a.getMensaje().toString());
                pref.setSummary(a.getNombreAlerta().toString());
                pref.setText(a.getNombreAlerta().toString());
            }else{
                prefe.setText("");
                pref.setText("");
            }
            helper.close();
            //prefe.setSummary(prefe.getText());
            prefe.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //final EditTextPreference pref = (EditTextPreference) findPreference("nombrealerta");
                    Log.d("Preferencia", preference.getKey());


                    prefe.setSummary(newValue.toString());
                    db.DateBaseHelper helper = new db.DateBaseHelper(AlertasConf.this);
                    RuntimeExceptionDao<Alertas, Integer> daoAlerta = helper.getDaoREalertas();

                    Alertas a = daoAlerta.queryForId(idRecibidoAlerta);
                    if (a != null) {

                        a.setMensaje(newValue.toString());
                        daoAlerta.update(a);
                        Log.d("Estoy editando", idRecibidoAlerta+"");
                        //Log.d("Alerta modificada", a.getPatronDetonante());

                    } else {
                        Alertas alert = new Alertas("", "", newValue.toString(), 1);
                        daoAlerta.create(alert);
                        idRecibidoAlerta = alert.getIdAlerta();
                    }
                    helper.close();
                    return true;
                }
            });


            //Nombre de alerta

            //final EditTextPreference pref = (EditTextPreference)findPreference("nombrealerta");
          // pref.setSummary(pref.getText());
            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //final EditTextPreference pref = (EditTextPreference) findPreference("nombrealerta");
                    Log.d("Preferencia", preference.getKey());


                    pref.setSummary(newValue.toString());
                    db.DateBaseHelper helper = new db.DateBaseHelper(AlertasConf.this);
                    RuntimeExceptionDao<Alertas, Integer> daoAlerta = helper.getDaoREalertas();

                    Alertas a = daoAlerta.queryForId(idRecibidoAlerta);
                    if (a != null) {

                        a.setNombreAlerta(newValue.toString());
                        daoAlerta.update(a);
                        //Log.d("Alerta modificada", a.getPatronDetonante());

                    } else {
                        Alertas alert = new Alertas(newValue.toString(), "", "", 1);
                        daoAlerta.create(alert);
                        idRecibidoAlerta = alert.getIdAlerta();
                    }
                    helper.close();
                    return true;
                }
            });

            //Contactos a alertar por Twitter

           // final PreferenceScreen prefeContactos = (PreferenceScreen)findPreference("personasTwitter");

            /*prefeContactos.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    mu = new ChipsMultiAutoCompleteTextview(getActivity());
                    alertDialog =  new AlertDialog.Builder(getActivity());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);


                    mu.setLayoutParams(lp);
                    alertDialog.setTitle("Usuarios a alertar.");
                    alertDialog.setMessage("Para enviar mensajes directos, dichos contactos deben seguirte.");
                    alertDialog.setView(mu);
                    alertDialog.setPositiveButton("Guardar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    Log.d("Hola", mu.getText().toString());
                                    dialog.dismiss();

                                }
                            });
                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog
                                    dialog.cancel();
                                }
                            });

                    mu.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                    alertDialog.show();

                    return false;

                }
            });*/


            //Mecanismo amigo vigilante key=amigos
            checkAmigosVigilantes = (SwitchPreference)findPreference("amigos");
            checkAmigosVigilantes.
                    setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if (newValue.toString().equals("true")) {
                                db.DateBaseHelper HelperMedioAmigos = new db.DateBaseHelper(AlertasConf.this);
                                RuntimeExceptionDao<MediosConexion, Integer> daoMedios = HelperMedioAmigos.getDaoREmedios();
                                MediosConexion medio = new MediosConexion(5, "", idRecibidoAlerta);
                                daoMedios.create(medio);

                                Toast.makeText(getApplicationContext(), "Mecanismo activado",
                                        Toast.LENGTH_SHORT).show();
                                HelperMedioAmigos.close();

                            } else {
                                db.DateBaseHelper HelperMedioAmigos = new db.DateBaseHelper(AlertasConf.this);
                                RuntimeExceptionDao<MediosConexion, Integer> daoMedios = HelperMedioAmigos.getDaoREmedios();
                                List<MediosConexion> eliminarMedioVigilante = null;
                                try {
                                    eliminarMedioVigilante = daoMedios.queryBuilder().where().eq("idAlerta",idRecibidoAlerta).and().eq("medio", 5).query();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                daoMedios.delete(eliminarMedioVigilante);

                                Toast.makeText(getApplicationContext(), "Mecanismo desactivado",
                                        Toast.LENGTH_SHORT).show();
                                HelperMedioAmigos.close();
                            }
                            return true;
                        }
                    });


            //Revision de chek en cada medio

            db.DateBaseHelper helperRevision = new db.DateBaseHelper(AlertasConf.this);
            RuntimeExceptionDao<MediosConexion, Integer> daoMedioRevision = helperRevision.getDaoREmedios();
            try {
                List<MediosConexion> listaMedios = daoMedios.queryBuilder().where().eq("idAlerta", idRecibidoAlerta).query();
                for (MediosConexion m: listaMedios) {
                    switch (m.getMedio()){
                        case 1: actionFB = true;
                            break;
                        case 2: actionTW = true;
                            break;
                        case 3: actionTG = true;
                            break;
                        case 5: actionAV = true;
                            break;
                        default:
                            break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            helperRevision.close();

            CheckBoxPreference fbchecked = (CheckBoxPreference) findPreference("facebook");
            CheckBoxPreference twchecked = (CheckBoxPreference) findPreference("twitter");
            SwitchPreference amchecked = (SwitchPreference) findPreference("amigos");
            CheckBoxPreference tgchecked = (CheckBoxPreference) findPreference("telegram");
            if(!actionFB){
                fbchecked.setChecked(false);
            }else{
                fbchecked.setChecked(true);
            }
            if(!actionTW){
                twchecked.setChecked(false);
            }else{
                twchecked.setChecked(true);
            }
            if(!actionTG){
                tgchecked.setChecked(false);
                personasTelegram.setEnabled(false);
            }else{
                tgchecked.setChecked(true);
                personasTelegram.setEnabled(true);
            }
            if(!actionAV){
                amchecked.setChecked(false);
            }else{
                amchecked.setChecked(true);
            }


            revisarAnterior();

        }

        public void mostrarAlertaConEspacios(){

            mu2 = new ChipsMultiAutoCompleteTextview(getActivity());
            alertDialog2 =  new AlertDialog.Builder(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);


            mu2.setLayoutParams(lp);
            db.DateBaseHelper helpertele = new db.DateBaseHelper(AlertasConf.this);
            RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helpertele.getDaoREmedios();
            try {
                MediosConexion contactos = daoMedios.queryBuilder().where().eq("idAlerta", idRecibidoAlerta).and().eq("medio", 3).queryForFirst();
                if(contactos!=null){
                    mu2.setText(contactos.getPersonas().toString());
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
            alertDialog2.setTitle("Usuarios a alertar.");
            alertDialog2.setMessage(checkTelegram.getSummary().toString()+", compartelo con tus amigos para recibir sus alertas.");
            alertDialog2.setView(mu2);
            alertDialog2.setPositiveButton("Guardar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            db.DateBaseHelper helperContactosTelegram = new db.DateBaseHelper(AlertasConf.this);
                            RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperContactosTelegram.getDaoREmedios();
                            try {
                                MediosConexion contactos = daoMedios.queryBuilder().where().eq("idAlerta",idRecibidoAlerta).and().eq("medio", 3).queryForFirst();
                                if(contactos!=null) {
                                    contactos.setPersonas(mu2.getText().toString());
                                    daoMedios.update(contactos);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            Log.d("Hola", mu2.getText().toString());
                            helperContactosTelegram.close();
                            dialog.dismiss();

                        }
                    });
            // Setting Negative "NO" Button
            alertDialog2.setNegativeButton("Cancelar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog
                            dialog.cancel();
                        }
                    });



            mu2.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            alertDialog2.show();
        }

        public void consultarEstatusConTelegram(){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://mpcdemexico.com.mx/app/revisarContactoBot.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Respuesta", response);
                            String[] respues = response.split("%");
                            if(!respues[0].equals("NO")){
                                checkTelegram.setSummary("Tu ID Telegram: "+respues[0]);
                                db.DateBaseHelper helperSumary = new db.DateBaseHelper(AlertasConf.this);
                                RuntimeExceptionDao<Parametros, Integer> daoParametros = helperSumary.getDaoREParametros();
                                daoParametros.create(new Parametros(1, respues[0], ""));
                                helperSumary.close();

                                mostrarAlertaConEspacios();

                            }else{
                                if(respues[1].equals("") || respues[1]==null){
                                    checkTelegram.setSummary("Alertar por este medio");
                                    db.DateBaseHelper helperSumary = new db.DateBaseHelper(AlertasConf.this);
                                    RuntimeExceptionDao<Parametros, Integer> daoParametros = helperSumary.getDaoREParametros();
                                    try {
                                        daoParametros.delete(daoParametros.queryBuilder().where().eq("id",1).query());
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    helperSumary.close();

                                    checkTelegram.setEnabled(false);
                                }else{
                                    Log.d("Todo bien", "Estado");
                                    mostrarAlertaConEspacios();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(AlertasConf.this,"¡Revisa tu conexión a internet!",Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("chatid", checkTelegram.getSummary().toString());
                    return params;
                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(AlertasConf.this);
            requestQueue.add(stringRequest);
        }

        public void revisarAnterior() {
            db.DateBaseHelper helper = new db.DateBaseHelper(AlertasConf.this);
            RuntimeExceptionDao<Alertas, Integer> daoAlerta = helper.getDaoREalertas();
            Alertas a = daoAlerta.queryForId(idRecibidoAlerta);
            if(a!=null){

                alertaVoz.setSummary(a.getPatronDetonante().toString());

            }
            helper.close();
        }

        public  boolean isConnected()
        {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo net = cm.getActiveNetworkInfo();
            if (net!=null && net.isAvailable() && net.isConnected()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            int contador=0;
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

                match_text_dialog = new Dialog(AlertasConf.this);
                match_text_dialog.setContentView(R.layout.dialog_matches_frag);
                match_text_dialog.setTitle("Selecciona");
                textlist = (ListView)match_text_dialog.findViewById(R.id.list);
                matches_text = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);



            /*for(int j=0; j<matches_text.size();j++) {
                matches_text.get(j).toLowerCase();
                if (matches_text.get(j).contains("hola soy bernardo")) {
                    contador++;
                }
            }

            if(contador>0){
                Intent intent = new Intent(TipoDos.this, ObtenerPoscion.class);
                startActivity(intent);
            }*/


                ArrayAdapter<String> adapter =    new ArrayAdapter<String>(AlertasConf.this,
                        android.R.layout.simple_list_item_1, matches_text);
                textlist.setAdapter(adapter);
                textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        alertaVoz.setSummary("" + matches_text.get(position));

                        //Aqui se puede guardar el patron


                        db.DateBaseHelper helper = new db.DateBaseHelper(AlertasConf.this);
                        RuntimeExceptionDao<Alertas, Integer> daoAlerta = helper.getDaoREalertas();

                        Alertas a = daoAlerta.queryForId(idRecibidoAlerta);
                        if (a != null) {

                           a.setPatronDetonante(matches_text.get(position));
                            daoAlerta.update(a);
                            Log.d("Alerta modificada", a.getPatronDetonante());

                        } else {
                            Alertas alert = new Alertas("", matches_text.get(position), "", 1);
                            daoAlerta.create(alert);

                            idRecibidoAlerta = alert.getIdAlerta();
                        }

                        helper.close();

                        match_text_dialog.hide();
                        Intent intent = new Intent(AlertasConf.this, Reconocimiento.class);
                        intent.putExtra("bandera", 1); //Con esta bandera el servicio debe actualizar la lista de frases almacenadas
                        startService(intent);
                    }
                });
                try{
                    match_text_dialog.show();
                }catch (RuntimeException e){

                }




            }
        }
    }

    private void llamadaEmergencia() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
        } else {
            Toast.makeText(this, "Permiso de llamada aceptado", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_PHONE_CALL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                llamadaEmergencia();
            } else {
                Toast.makeText(this, "Sorry!!! Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
