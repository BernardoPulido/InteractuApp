package com.example.bernardo.seguridadpersonal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bernardo.seguridadpersonal.Configuraciones.Principal_Configuracion;
import com.example.bernardo.seguridadpersonal.Configuraciones.Reconocimiento;
import com.example.bernardo.seguridadpersonal.Configuraciones.customHandler;
import com.example.bernardo.seguridadpersonal.PantallaPrincipal.AmigoVigilante;
import com.example.bernardo.seguridadpersonal.PantallaPrincipal.Historial;
import com.example.bernardo.seguridadpersonal.PantallaPrincipal.HowWork;
import com.example.bernardo.seguridadpersonal.PantallaPrincipal.Inicial;
import com.example.bernardo.seguridadpersonal.PantallaPrincipal.Tutorial;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.pushbots.push.Pushbots;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.Alertas;
import db.HistorialAlertas;
import db.Parametros;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final int TOTAL_PAGES = 5;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    boolean isOpaque = true;

    public Profile profile;

    private static final String REGISTER_URL = "http://mpcdemexico.com.mx/app/volleyRegister.php";
    private static final String GETUSER_URL = "http://mpcdemexico.com.mx/app/getUser.php";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_TIPO = "tipo";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_APELLIDO = "apellido";
    public static final String KEY_TOKEN= "token";
    private int Llamar_OK =0;
    private String NumeroLlamada="4921027963";
    double longitudeNetwork, latitudeNetwork;
    public ImageView img;
    CardView cardMain;
    String amigoDialogContenido;
    LinearLayout tuto;
    FloatingActionButton fab;
    TextView InicioNombreUser;
    TextView InicioApellidoUser;
    LocationManager locationManager;
    TextView InicioEdadUser;
    TextView InicioEmailUser;

    private boolean listening = false;
    private boolean permissionToRecordAccepted = false;
    private static String TAG = "MainActivity";
    private static final int RECORD_REQUEST_CODE = 101;
    private MicrophoneInputStream capture;
    private MicrophoneHelper microphoneHelper;
    private SpeechToText speechService;

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Inicio");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       this.latitudeNetwork=31.8693752;
       this.longitudeNetwork=-116.6689002;

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setTag("inicio");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        microphoneHelper = new MicrophoneHelper(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fab.getTag().equals("Historial")){
                    AlertDialog.Builder builderaux = new AlertDialog.Builder(MainActivity.this);

                    builderaux.setTitle("¿Deseas limpiar las interacciones?")
                            .setMessage("Se eliminarán todos los registros.")
                            .setPositiveButton("Si",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            db.DateBaseHelper helperHistorialeliminar = new db.DateBaseHelper(MainActivity.this);
                                            RuntimeExceptionDao<HistorialAlertas, Integer> daoHistorial = helperHistorialeliminar.getDaoREhistorial();
                                            daoHistorial.delete(daoHistorial.queryForAll());
                                            cargarHistorial();
                                        }
                                    })
                            .setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                    builderaux.show();


                }else{
                   recordMessage(view);
                }

            }
        });

        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(this);
        }
        profile = Profile.getCurrentProfile(); //Revisar que el profile no sea nulo
        cardMain = (CardView)findViewById(R.id.IntentoMain);
        cardMain.setVisibility(View.INVISIBLE);
        tuto = (LinearLayout)findViewById(R.id.PadreMain);

        //Valores Preview Index


        InicioNombreUser = (TextView)findViewById(R.id.menuinicioNombreUser);
       InicioApellidoUser = (TextView)findViewById(R.id.menuinicioApellidoUser);
        InicioEdadUser = (TextView)findViewById(R.id.menuinicioNEdadUser);
        InicioEmailUser = (TextView)findViewById(R.id.menuinicioCorreoUser);


        if(getIntent().getIntExtra("inicial",0)==1) {
            registroAutomaticoFb();
        }

        db.DateBaseHelper helper = new db.DateBaseHelper(MainActivity.this);
        RuntimeExceptionDao<Alertas, Integer> daoAlerta = helper.getDaoREalertas();
        List<Alertas> listaMostrarInicio = daoAlerta.queryForAll();

        boolean flahauxfirst=false;
        int flagLimite=0;
        for (Alertas alr:listaMostrarInicio) {

            if(flagLimite<2) {
                if (!flahauxfirst) {

                    flahauxfirst = true;
                }
                flagLimite++;
            }

        }


        RuntimeExceptionDao<Parametros, Integer> daoParametros = helper.getDaoREParametros();
        List<Parametros> parametros = daoParametros.queryForAll();
        for (Parametros p:parametros) {
            switch (p.getId()){
                case 2: InicioEmailUser.setText(p.getDescripcion());
                    break;
                case 3:
                    break;
                case 4: InicioNombreUser.setText(p.getDescripcion());
                    break;
                case 5: InicioApellidoUser.setText(p.getDescripcion());
                    break;
                case 6:
                    if(!p.getDescripcion().equals("0")){
                        InicioEdadUser.setText(p.getDescripcion()+" años");
                    }

                    break;

            }
        }
        helper.close();
        toggleNetworkUpdates(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Window window = getWindow();

        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlideAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        ini();

        if(getIntent().getIntExtra("history", 0)==1){
            setTitle("Historial");
            tuto.setVisibility(View.INVISIBLE);
            cardMain.setVisibility(View.VISIBLE);
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_delete_sweep_black_24dp));
            String tag = "Historial";
            fab.setTag(tag);
            fab.setVisibility(View.VISIBLE);
            android.app.FragmentManager fm = getFragmentManager();
            android.app.FragmentTransaction ft = fm.beginTransaction();
            Historial tff = new Historial();
            ft.replace(R.id.IntentoMain, tff, "SETTING");
            ft.commit();
            //InteractuApp
        }

        if(getIntent().getIntExtra("confAlertas", 0)==1){
            setTitle("Configuración");
            tuto.setVisibility(View.INVISIBLE);
            cardMain.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);
            android.app.FragmentManager fm = getFragmentManager();
            android.app.FragmentTransaction ft = fm.beginTransaction();

            Principal_Configuracion tff = new Principal_Configuracion();
            ft.replace(R.id.IntentoMain, tff, "SETTING");
            ft.commit();
        }
        if(getIntent().getIntExtra("comeperfil", 0)==1){

            setTitle("Configuración");
            tuto.setVisibility(View.INVISIBLE);
            cardMain.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);
            android.app.FragmentManager fm = getFragmentManager();
            android.app.FragmentTransaction ft = fm.beginTransaction();

            Principal_Configuracion tff = new Principal_Configuracion();
            ft.replace(R.id.IntentoMain, tff, "SETTING");
            ft.commit();
            if(getIntent().getIntExtra("perfil", 0)==1) {
                try{
                    GuardarCambiosPerfil();
                }catch (SQLException e){

                }

            }
        }

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        if(getIntent().getIntExtra("inicial", 0)==1){
            stopService(new Intent(MainActivity.this, Reconocimiento.class));
            startService(new Intent(this,Reconocimiento.class));
        }

    }
    public void toggleNetworkUpdates(boolean enter) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_DENIED) {
            if (enter) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 20 * 1000, 10, locationListenerNetwork);
            } else {
                locationManager.removeUpdates(locationListenerNetwork);
            }
        }
    }
    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeNetwork = location.getLongitude();
            latitudeNetwork = location.getLatitude();
            Log.d("POSICION", latitudeNetwork+", "+longitudeNetwork);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {

        }
        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void GuardarCambiosPerfil() throws SQLException {
        db.DateBaseHelper helperUpdatePerfil = new db.DateBaseHelper(MainActivity.this);
        RuntimeExceptionDao<Parametros, Integer> daoUpdatePerfil = helperUpdatePerfil.getDaoREParametros();
        Parametros nombreUpdatePerfil = daoUpdatePerfil.queryBuilder().where().eq("id", 4).queryForFirst();
        Parametros apellidoUpdatePerfil = daoUpdatePerfil.queryBuilder().where().eq("id", 5).queryForFirst();
        Parametros edadUpdatePerfil = daoUpdatePerfil.queryBuilder().where().eq("id", 6).queryForFirst();
        Parametros id = daoUpdatePerfil.queryBuilder().where().eq("id", 3).queryForFirst();
        final String nombreUpdate = nombreUpdatePerfil.getDescripcion();
        final String apellidoUpdate = apellidoUpdatePerfil.getDescripcion();
        final String edadUpdate = edadUpdatePerfil.getDescripcion();
        final String idUpdate = id.getDescripcion();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://mpcdemexico.com.mx/app/guardarCambiosPerfil.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"¡Ocurrio un error al editar perfil! Revisa tu conexión a internet",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("nombre", nombreUpdate);
                params.put("apellido", apellidoUpdate);
                params.put("edad", edadUpdate);
                params.put("id", idUpdate);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


        Log.d("Editando perfil", "OK");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this,Reconocimiento.class));
            } else {
                //finish();
            }
        }
    }

    public void consultarUsuarioRegistrado(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://mpcdemexico.com.mx/app/volleyConsulta.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String mensajeMostrar="";
                        String[] respuesta = response.split("%");
                        if(respuesta[0].equals("1")){
                            db.DateBaseHelper helper = new db.DateBaseHelper(MainActivity.this);
                            RuntimeExceptionDao<db.AmigoVigilante, Integer> daoAmigoVigilante = helper.getDaoREamigo();

                            try {
                                List<db.AmigoVigilante> listaAmigos = daoAmigoVigilante.queryBuilder().where().eq("correoAmigoReceptor", amigoDialogContenido).query();
                                if(listaAmigos.size()==0){

                                    db.AmigoVigilante amigo = new db.AmigoVigilante();
                                    amigo.setAmigoRecepetor(Integer.parseInt(respuesta[1]));
                                    amigo.setAmigoEmisor(0);
                                    amigo.setCorreoAmigoReceptor(amigoDialogContenido);
                                    amigo.setCorreoAmigoEmisor("");
                                    amigo.setMensaje("Este sera un codigo de PushBots");
                                    amigo.setEstatus(0);
                                    daoAmigoVigilante.create(amigo);
                                    mensajeMostrar="¡Amigo guardado!";
                                    cargarAmigos();
                                }else{
                                    mensajeMostrar="¡Error: Amigo ya existente!";
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            helper.close();
                        }else if(respuesta[0].equals("2")){
                            db.DateBaseHelper helper = new db.DateBaseHelper(MainActivity.this);
                            RuntimeExceptionDao<db.AmigoVigilante, Integer> daoAmigoVigilante = helper.getDaoREamigo();

                            try {
                                List<db.AmigoVigilante> listaAmigos = daoAmigoVigilante.queryBuilder().where().eq("amigoRecepetor", amigoDialogContenido).query();
                                if(listaAmigos.size()==0){
                                    db.AmigoVigilante amigo = new db.AmigoVigilante();
                                    amigo.setAmigoRecepetor(Integer.parseInt(amigoDialogContenido));
                                    amigo.setAmigoEmisor(0);
                                    amigo.setCorreoAmigoReceptor(respuesta[1]);
                                    amigo.setCorreoAmigoEmisor("");
                                    amigo.setMensaje("Este sera codigo de PusBots");
                                    amigo.setEstatus(0);
                                    daoAmigoVigilante.create(amigo);
                                    mensajeMostrar="¡Amigo guardado!";
                                   cargarAmigos();

                                }else{
                                    mensajeMostrar="¡Error: Amigo ya existente!";
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            helper.close();
                        }else{
                            mensajeMostrar="¡Error, ID o Email invalidos!";
                        }
                        Toast.makeText(MainActivity.this, mensajeMostrar, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"¡Revisa tu conexión a internet!",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("amigo", amigoDialogContenido);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void cargarAmigos(){
        setTitle("Amigo vigilante");
        tuto.setVisibility(View.INVISIBLE);
        cardMain.setVisibility(View.VISIBLE);
        fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_add_black_24dp));
        fab.setTag("Amigos");
        fab.setVisibility(View.VISIBLE);
        android.app.FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction ft = fm.beginTransaction();

        AmigoVigilante tff = new AmigoVigilante();
        ft.replace(R.id.IntentoMain, tff, "SETTING");
        ft.commit();
    }
    public void cargarHistorial(){
        setTitle("Historial");
        tuto.setVisibility(View.INVISIBLE);
        cardMain.setVisibility(View.VISIBLE);
        fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_delete_sweep_black_24dp));
        String tag = "Historial";
        fab.setTag(tag);
        fab.setVisibility(View.VISIBLE);
        android.app.FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction ft = fm.beginTransaction();
        Historial tff = new Historial();
        ft.replace(R.id.IntentoMain, tff, "SETTING");
        ft.commit();
    }

    private void llamadaEmergencia() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Ocurrio un problema al recuperar los permisos necesarios.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Realizando llamada, espera un momento.", Toast.LENGTH_SHORT).show();

            String number = this.NumeroLlamada;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
        }
    }
    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_enter_amigo, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Agregar amigo vigilante");
        dialogBuilder.setMessage("Ingresa ID o Email");
        dialogBuilder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                amigoDialogContenido = edt.getText().toString();
                consultarUsuarioRegistrado();

            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //No
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();


    }
    private void buildCircles() {
        circles = LinearLayout.class.cast(findViewById(R.id.circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for (int i = 0; i < TOTAL_PAGES; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_brightness_1_black_24dp);

            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            circles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index) {
        if (index < TOTAL_PAGES) {
            for (int i = 0; i < TOTAL_PAGES; i++) {
                ImageView circle = (ImageView) circles.getChildAt(i);
                if (i == index) {
                    int activeColor = Color.parseColor("#607D8B");
                    circle.setColorFilter(activeColor);
                } else {
                    int color = Color.parseColor("#ECF0F1");
                    circle.setColorFilter(color);
                }
            }
        }
    }

    public void registroAutomaticoFb() {
        if(profile!=null){
            String nombre = profile.getFirstName();
            String apellido = profile.getLastName();
            String link = profile.getLinkUri().toString();
            db.DateBaseHelper helperFB = new db.DateBaseHelper(MainActivity.this);
            RuntimeExceptionDao<Parametros, Integer> daoParametros = helperFB.getDaoREParametros();
            String correo="example@hotmail.com";
            try {
                Parametros emailFB = daoParametros.queryBuilder().where().eq("id", 2).queryForFirst();
                if(emailFB!=null) {
                    correo = emailFB.getDescripcion();
                }else{
                    correo="example@hotmail.com";
                }
            }catch (SQLException e){}
            helperFB.close();
            int tipo = 1;
            registrar(link, correo, nombre, apellido, tipo);


        }else{
            if(getIntent().getStringExtra("username")!=null){
                altaUser(getIntent().getStringExtra("username"));
            }
        }
    }

    public void altaUser(String link){
        final String user = link;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GETUSER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] respuesta = response.split("%");
                        if(respuesta.length>0) {
                            Log.d("QUe pedo", respuesta[0].toString());

                            InicioNombreUser.setText(respuesta[1]);
                            InicioApellidoUser.setText(respuesta[2]);
                            if (!respuesta[3].equals("0")) {
                                InicioEdadUser.setText(respuesta[3] + " años");
                            }
                            InicioEmailUser.setText(respuesta[4]);
                        }

                        db.DateBaseHelper helperUser = new db.DateBaseHelper(MainActivity.this);
                        RuntimeExceptionDao<Parametros, Integer> daoParametros = helperUser.getDaoREParametros();
                        try {
                            Parametros idUsuario = daoParametros.queryBuilder().where().eq("id", 3).queryForFirst();
                            if (idUsuario != null) {
                                idUsuario.setDescripcion(respuesta[0]);
                                daoParametros.update(idUsuario);

                            }else{
                                daoParametros.create(new Parametros(3, respuesta[0], ""));
                            }

                            Parametros nombre = daoParametros.queryBuilder().where().eq("id", 4).queryForFirst();
                            if (nombre != null) {
                                nombre.setDescripcion(respuesta[1]);
                                daoParametros.update(nombre);
                            }else{
                                daoParametros.create(new Parametros(4, respuesta[1], ""));
                            }

                            Parametros apellidos = daoParametros.queryBuilder().where().eq("id", 5).queryForFirst();
                            if (apellidos != null) {
                                apellidos.setDescripcion(respuesta[2]);
                                daoParametros.update(apellidos);
                            }else{
                                daoParametros.create(new Parametros(5, respuesta[2], ""));
                            }

                            Parametros edad = daoParametros.queryBuilder().where().eq("id", 6).queryForFirst();
                            if (edad != null) {
                                edad.setDescripcion(respuesta[3]);
                                daoParametros.update(edad);
                            }else{
                                daoParametros.create(new Parametros(6, respuesta[3], ""));
                            }

                            Parametros username = daoParametros.queryBuilder().where().eq("id", 7).queryForFirst();
                            if (username != null) {
                                username.setDescripcion(user);
                                daoParametros.update(username);
                            }else{
                                daoParametros.create(new Parametros(7, user, ""));
                            }

                            Parametros email = daoParametros.queryBuilder().where().eq("id", 2).queryForFirst();
                            if (email != null) {
                                email.setDescripcion(respuesta[4]);
                                daoParametros.update(email);
                            }else{
                                daoParametros.create(new Parametros(2, respuesta[4], ""));
                            }

                        }catch(SQLException e){}
                        helperUser.close();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"¡Revisa tu conexión a internet!",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("link",user);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void registrar(final String username, String email, String nombre, String apellido, int tipo){
        final String user = username;
        final String e = email;
        final String nom = nombre;
        final String ape = apellido;
        final String tip = tipo+"";

        String tokens;
        if(Pushbots.sharedInstance().isInitialized()){
           tokens = Pushbots.sharedInstance().regID();
        }else{
            Pushbots.sharedInstance().init(this);
            Pushbots.sharedInstance().setCustomHandler(customHandler.class);
            tokens = Pushbots.sharedInstance().regID();
        }

        final String token = tokens;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        altaUser(user);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"¡Revisa tu conexión a internet!",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_USERNAME,user);

                params.put(KEY_EMAIL, e);
                params.put(KEY_NOMBRE, nom);
                params.put(KEY_APELLIDO, ape);
                params.put(KEY_TIPO, tip);
                params.put(KEY_TOKEN, token);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (pager.getCurrentItem() == 0) {
                super.onBackPressed();
            } else {
                pager.setCurrentItem(pager.getCurrentItem() - 1);
            }
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //Settings
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.inicio) {

            fab.setVisibility(View.VISIBLE);
            setTitle("Inicio");
            toggleNetworkUpdates(true);
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_record_voice_over_black_24dp));
            fab.setTag("inicio");
            tuto.setVisibility(View.VISIBLE);
            cardMain.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);
            android.app.FragmentManager fm = getFragmentManager();
            android.app.FragmentTransaction ft = fm.beginTransaction();

            Tutorial tff = new Tutorial();
            ft.replace(R.id.IntentoMain, tff, "SETTING");
            ft.commit();


        } else if (id == R.id.nav_slideshow) {
            toggleNetworkUpdates(false);
            setTitle("Configuración");
            tuto.setVisibility(View.INVISIBLE);
            cardMain.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);
            android.app.FragmentManager fm = getFragmentManager();
            android.app.FragmentTransaction ft = fm.beginTransaction();

            Principal_Configuracion tff = new Principal_Configuracion();
            ft.replace(R.id.IntentoMain, tff, "SETTING");
            ft.commit();

        } else if (id == R.id.historial) {
            setTitle("Interacciones");
            toggleNetworkUpdates(false);
            tuto.setVisibility(View.INVISIBLE);
            cardMain.setVisibility(View.VISIBLE);
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_delete_sweep_black_24dp));
            String tag = "Historial";
            fab.setTag(tag);
            fab.setVisibility(View.VISIBLE);
            android.app.FragmentManager fm = getFragmentManager();
            android.app.FragmentTransaction ft = fm.beginTransaction();

            Historial tff = new Historial();
            ft.replace(R.id.IntentoMain, tff, "SETTING");
            ft.commit();

        } else if (id == R.id.nav_share) {

            Intent in = new Intent(this, HowWork.class);
            startActivity(in);

        } else if (id == R.id.cerrar) {
            toggleNetworkUpdates(false);
            db.DateBaseHelper helperUser = new db.DateBaseHelper(MainActivity.this);
            RuntimeExceptionDao<Parametros, Integer> daoParametros = helperUser.getDaoREParametros();
            try {
                Parametros idUsuario = daoParametros.queryBuilder().where().eq("id", 3).queryForFirst();
                if(idUsuario!=null){
                    idUsuario.setDescripcion("");
                    daoParametros.update(idUsuario);
                }
            }catch (SQLException e){}
            helperUser.close();
            //Cerrar servicio
            stopService(new Intent(MainActivity.this, Reconocimiento.class));
            //Borrar instancia de Facebook
            LoginManager.getInstance().logOut();
            Intent in = new Intent(this, Principal.class);
            startActivity(in);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ini() {

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == TOTAL_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        pager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildCircles();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
           return true;

        }
        return super.onKeyDown(keyCode, event);

    }



    private class ScreenSlideAdapter extends FragmentStatePagerAdapter {

        public ScreenSlideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Inicial tutorialFragment = null;

            switch (position) {
                case 0:
                    tutorialFragment = Inicial.newInstance(
                            R.layout.fragment_screen2);
                    break;
                case 1:
                    tutorialFragment = Inicial.newInstance(
                            R.layout.fragment_screen3);
                    break;
                case 2:
                    tutorialFragment = Inicial.newInstance(
                            R.layout.fragment_screen5);
                    break;
                case 3:
                    tutorialFragment = Inicial.newInstance(
                            R.layout.fragment_screen6);
                    break;
                case 4:
                    tutorialFragment = Inicial.newInstance(
                            R.layout.fragment_screen1);
                    break;


            }

            return tutorialFragment;
        }

        @Override
        public int getCount() {
            return TOTAL_PAGES;
        }
    }
    private void watson(final String t) {

        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {
                    NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                            "2018-03-19",
                            "96a482dc-10cd-46a3-860a-1855e4baf1cf",
                            "iEfTDLnp3rEQ"
                    );

                    //String text = "Hola mi nombre es Bernardo, hace muchos años comence a desarrollar aplicaciones en esta plataforma y me sorprendió la cantidad de herrameintas que se proveen para que los desarrolladores puedan ampliar su imaginación y generar nuevas y utiles herrmaientas para la vida cotidiana.";
                    String text = t;
                    EntitiesOptions entitiesOptions = new EntitiesOptions.Builder()
                            .emotion(true)
                            .sentiment(true)
                            .limit(2)
                            .build();
                    KeywordsOptions keywordsOptions = new KeywordsOptions.Builder()
                            .emotion(true)
                            .sentiment(true)
                            .limit(5)
                            .build();
                    ConceptsOptions conceptos = new ConceptsOptions.Builder()
                            .limit(5)
                            .build();
                    SentimentOptions sentiments = new SentimentOptions.Builder()
                            .document(true)
                            .build();
                    EmotionOptions emotions = new EmotionOptions.Builder()
                            .document(true)
                            .build();

                    Features features = new Features.Builder()
                            .entities(entitiesOptions)
                            .keywords(keywordsOptions)
                            .sentiment(sentiments)
                            .emotion(emotions)
                            .concepts(conceptos)
                            .build();

                    AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                            .text(text)
                            .features(features)
                            .build();

                    AnalysisResults response = service
                            .analyze(parameters)
                            .execute();
                    Log.d("Respuesta: ", response.toString());
                    Log.d("Sentimientos: ", response.getSentiment().getDocument().getLabel());
                    saveRegistro(response, response.getSentiment().getDocument().getScore());
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void saveRegistro(AnalysisResults s, Double score) {
        String sentimiento="";
        if(score<0){
            sentimiento="Negativo: "+score;
        }else if(score>0){
            sentimiento="Positivo: "+score;
        }else{
            sentimiento="Neutral";
        }

        //Detectadno Keywords
        String palabras="";
        if(!s.getKeywords().isEmpty() && s.getKeywords().size()!=0){
            Log.d("KEY", s.getKeywords().get(0).getText());
            palabras=s.getKeywords().get(0).getText();
        }else{
            palabras="No se identificaron palabras clave";
        }


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedDate = df.format(c.getTime());

        db.DateBaseHelper helperHistorial = new db.DateBaseHelper(MainActivity.this);
        RuntimeExceptionDao<HistorialAlertas, Integer> daoHistorial = helperHistorial.getDaoREhistorial();
        daoHistorial.create(new HistorialAlertas(sentimiento, " ", palabras, "4, "+this.latitudeNetwork+", "+this.longitudeNetwork, formattedDate+"", "", 0));
        //this.usuarioFinal+", "+this.lat+", "+this.lon
        helperHistorial.close();
    }

    //Record a message via Watson Speech to Text
    private void recordMessage(View v) {

        speechService = new SpeechToText();
        speechService.setUsernameAndPassword("6dd88eed-c55e-43e8-b63f-57afd0e5374e", "WvMhkFa5ZXwj");

        if(listening != true) {
            capture = microphoneHelper.getInputStream(true);
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        speechService.recognizeUsingWebSocket(capture, getRecognizeOptions(), new MicrophoneRecognizeDelegate());
                    } catch (Exception e) {
                        showError(e);
                    }
                }
            }).start();
            listening = true;
            Snackbar.make(v, "Analizando interacción, para terminar presione nuevamante.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();


        } else {
            try {
                microphoneHelper.closeInputStream();
                listening = false;
                Snackbar.make(v, "Análisis detenido, consulte el apartado 'Interacciones'.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    //Private Methods - Speech to Text
    private RecognizeOptions getRecognizeOptions() {
        return new RecognizeOptions.Builder()
                .contentType(ContentType.OPUS.toString())
                .model("es-ES_BroadbandModel")
                //.model("en-UK_NarrowbandModel")
                .interimResults(false)
                .inactivityTimeout(2000)
                .build();
    }
    private class MicrophoneRecognizeDelegate implements RecognizeCallback {
        @Override
        public void onTranscription(SpeechResults speechResults) {
            Log.d("RESULT", speechResults.toString());
            String texto="";
            for (Transcript t: speechResults.getResults()){
                texto = texto + t.getAlternatives().get(0).getTranscript();
            }

            Log.d("ONLY", texto);
            showMicText(texto);
            watson(texto);

        }

        @Override public void onConnected() {

        }

        @Override public void onError(Exception e) {
            showError(e);

        }

        @Override public void onDisconnected() {

        }

        @Override
        public void onInactivityTimeout(RuntimeException runtimeException) {

        }

        @Override
        public void onListening() {

        }

        @Override
        public void onTranscriptionComplete() {
            Log.d("Complete", "OK");
        }
    }

    private void showMicText(final String text) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                //Set Global
            }
        });
    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

}
