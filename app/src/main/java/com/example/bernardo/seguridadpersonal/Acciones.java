package com.example.bernardo.seguridadpersonal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.bernardo.seguridadpersonal.Configuraciones.NotificacionActivado;
import com.example.bernardo.seguridadpersonal.Configuraciones.Reconocimiento;
import com.example.bernardo.seguridadpersonal.Twitter.TweetPost;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.sql.SQLException;

import db.Alertas;
import db.AmigoVigilante;
import db.HistorialAlertas;
import db.MediosConexion;
import db.Parametros;


public class Acciones extends AppCompatActivity {

    private String message;
    private ShareContent shareContent;
    private int idRecibidoAlerta;
    LocationManager locationManager;
    Alertas alerta;
    MediosConexion actionFB=null;
    MediosConexion actionTW=null;
    MediosConexion actionTG=null;
    MediosConexion actionLL=null;
    MediosConexion actionAV=null;
    String mediosForHistorial="";
    String lat="23.0727536", lon="-104.7926761";
    String usuarioFinal = "Unknown";

    private static final String PERMISSION = "publish_actions";
    private static final int PERMISSIONS_REQUEST_PHONE_CALL = 99;
    private static final int PERMISSIONS_REQUEST_PHONE_GPS = 99;
    private static String[] PERMISSIONS_PHONECALL = {Manifest.permission.CALL_PHONE};
    private static final Location SEATTLE_LOCATION = new Location("") {
        {
            setLatitude(47.6097);
            setLongitude(-47.6097);

        }
    };

    private final String PENDING_ACTION_BUNDLE_KEY =
            "com.example.acciones:PendingAction";

    private PendingAction pendingAction = PendingAction.NONE;
    private boolean canPresentShareDialog;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ShareDialog shareDialog;
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("HelloFacebook", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
            String title = getString(R.string.error);
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("HelloFacebook", "Success!");
            if (result.getPostId() != null) {
                String title = getString(R.string.success);
                String id = result.getPostId();
                String alertMessage = getString(R.string.successfully_posted_post, id);
               // showResult(title, alertMessage);
            }
        }

        private void showResult(String title, String alertMessage) {
            new AlertDialog.Builder(Acciones.this)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    };

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }


    private static final String FACEBOOK_APPID = "PUT YOUR FACEBOOK APPID HERE";
    private static final String FACEBOOK_PERMISSION = "publish_stream";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtenemos ID alerta y almacenamos la alerta
        this.idRecibidoAlerta = getIntent().getIntExtra("id", 1);
        Log.d("Alerta No: ", idRecibidoAlerta+"");
        db.DateBaseHelper helper = new db.DateBaseHelper(Acciones.this);
        RuntimeExceptionDao<Alertas, Integer> daoAlertas = helper.getDaoREalertas();
        this.alerta = daoAlertas.queryForId(this.idRecibidoAlerta);
        helper.close();


        FacebookSdk.sdkInitialize(this.getApplicationContext());
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Obtiene la ultima posición registrada

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_PHONE_GPS);
            Log.d("Permiso", " No se tiene");
        } else {
            Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(l!=null) {
                lat = String.valueOf(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
                lon = String.valueOf(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());
            }
        }

        if(getIntent().getIntExtra("notify", 0)==1){
            NotificacionActivado.cancel(this);
        }
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handlePendingAction();
                        updateUI();
                    }

                    @Override
                    public void onCancel() {
                        if (pendingAction != PendingAction.NONE) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        updateUI();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (pendingAction != PendingAction.NONE
                                && exception instanceof FacebookAuthorizationException) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        updateUI();
                    }

                    private void showAlert() {
                        new AlertDialog.Builder(Acciones.this)
                                .setTitle(R.string.cancelled)
                                .setMessage(R.string.permission_not_granted)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                });

        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }

        setContentView(R.layout.activity_acciones);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateUI();

                handlePendingAction();
            }
        };

        db.DateBaseHelper helpermedios = new db.DateBaseHelper(Acciones.this);
        RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helpermedios.getDaoREmedios();
        try {
            List<MediosConexion> listaMedios = daoMedios.queryBuilder().where().eq("idAlerta",this.idRecibidoAlerta).query();
            for (MediosConexion m: listaMedios) {
                switch (m.getMedio()){
                    case 1: actionFB = m;
                        if(mediosForHistorial.equals("")){
                            mediosForHistorial="Facebook";
                        }else{
                            mediosForHistorial=mediosForHistorial+", Facebook";
                        }
                        break;
                    case 2: actionTW = m;
                        if(mediosForHistorial.equals("")){
                            mediosForHistorial="Twitter";
                        }else{
                            mediosForHistorial=mediosForHistorial+", Twitter";
                        }
                        break;
                    case 3: actionTG = m;
                        if(mediosForHistorial.equals("")){
                            mediosForHistorial="Telegram";
                        }else{
                            mediosForHistorial=mediosForHistorial+", Telegram";
                        }
                        break;
                    case 4: actionLL = m;
                        if(mediosForHistorial.equals("")){
                            mediosForHistorial="Llamada";
                        }else{
                            mediosForHistorial=mediosForHistorial+", Llamada";
                        }
                        break;
                    case 5: actionAV = m;
                        if(mediosForHistorial.equals("")){
                            mediosForHistorial="Amigo";
                        }else{
                            mediosForHistorial=mediosForHistorial+", Amigo";
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        helpermedios.close();

        db.DateBaseHelper helperUser = new db.DateBaseHelper(Acciones.this);
        RuntimeExceptionDao<Parametros, Integer> daoParametros = helperUser.getDaoREParametros();
        try {
            this.usuarioFinal  = daoParametros.queryBuilder().where().eq("id", 4).queryForFirst().getDescripcion();
        }catch (SQLException e){}
        helperUser.close();

        //Guardar registro en historial
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedDate = df.format(c.getTime());

        db.DateBaseHelper helperHistorial = new db.DateBaseHelper(Acciones.this);
        RuntimeExceptionDao<HistorialAlertas, Integer> daoHistorial = helperHistorial.getDaoREhistorial();
        daoHistorial.create(new HistorialAlertas(mediosForHistorial, this.alerta.getPatronDetonante(), this.alerta.getMensaje(), this.usuarioFinal+", "+this.lat+", "+this.lon, formattedDate+"", "", 0));
        helperHistorial.close();

        if(actionFB!=null){
            //Activa la publicacion FB
            onClickPostStatusUpdate();
        }
        if(actionTG!=null){
            //Envia mensajes por Telegram
            enviarMensajesTelegram();
        }
        if(actionAV!=null){
            //Enviar Push Amigo Vigilante
            enviarPushAmigosVigilantes();
        }
        stopService(new Intent(Acciones.this, Reconocimiento.class));
        startService(new Intent(Acciones.this,Reconocimiento.class));
        if(actionTW!=null){
            //Post on Twiiter
            Intent intentnuevo = new Intent(this, TweetPost.class);
            if(actionLL!=null){
                intentnuevo.putExtra("llamada", 1);
                intentnuevo.putExtra("num", actionLL.getPersonas());
            }

            intentnuevo.putExtra("usuarioFinal", this.usuarioFinal);
            intentnuevo.putExtra("lat", this.lat);
            intentnuevo.putExtra("lon", this.lon);
            intentnuevo.putExtra("mensaje", this.alerta.getMensaje());
            startActivity(intentnuevo);
        }else{
            if(actionLL!=null){
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("llamar", 1);

                intent.putExtra("numero", actionLL.getPersonas());
                startActivity(intent);

            }else{
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }

        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);

    }

    private void enviarPushAmigosVigilantes() {

        final String latitud=lat;
        final String longitud= lon;
        final String usuarioFinall =this.usuarioFinal;
        String mensajeAlerta;
        if(alerta.getMensaje()==null || alerta.getMensaje().equals("")){
            mensajeAlerta = usuarioFinall+" esta en peligro, contactalo.";
        }else{
            mensajeAlerta = alerta.getMensaje();
        }
        final String mensajeFinal = mensajeAlerta;


        db.DateBaseHelper helperPush = new db.DateBaseHelper(Acciones.this);
        RuntimeExceptionDao<MediosConexion, Integer> daoMedios = helperPush.getDaoREmedios();
        String arregloIds="";
        try {
            List<MediosConexion> validacion = daoMedios.queryBuilder().where().eq("medio", 5).query(); //Resta agregar el idAlerta
            if(validacion!=null){
                RuntimeExceptionDao<AmigoVigilante, Integer> daoAmigosPush = helperPush.getDaoREamigo();
                List<AmigoVigilante> listaAmigos = daoAmigosPush.queryForAll();
                for(int i=0; i<listaAmigos.size(); i++){
                    if(listaAmigos.get(i).getEstatus()==0){
                        arregloIds=arregloIds+listaAmigos.get(i).getAmigoRecepetor()+" ";
                    }
                }
            }
        }catch (SQLException e){

        }
        final String arregloAmigos = arregloIds;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://mpcdemexico.com.mx/app/PushOne.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Respuesta Push: ", response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Acciones.this,"Hubo un error, revisa tu conexión a internet",Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("lista", arregloAmigos);
                params.put("mensaje", mensajeFinal);
                params.put("user", usuarioFinall);
                params.put("lat", latitud);
                params.put("lon", longitud);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(Acciones.this);
        requestQueue.add(stringRequest);

    }

    private void enviarMensajesTelegram() {
        final String mensajeAlerta;
        if(alerta.getMensaje()==null || alerta.getMensaje().equals("")){
            mensajeAlerta = usuarioFinal+" esta en peligro, contactalo.";
        }else{
            mensajeAlerta = alerta.getMensaje();
        }

            final MediosConexion contactos = actionTG;
            if(contactos!=null) {
                //Enviar a PHP para que envie cada mensaje
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://mpcdemexico.com.mx/app/enviarMensajesTelegram.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Respuesta", response);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Acciones.this,"¡Revisa tu conexión a internet!",Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("arregloContactos", contactos.getPersonas().toString());
                        params.put("mensaje", mensajeAlerta);
                        params.put("latitud", lat);
                        params.put("longitud", lon);
                        params.put("usuarioNombre", usuarioFinal);
                        return params;
                    }

                };
                RequestQueue requestQueue = Volley.newRequestQueue(Acciones.this);
                requestQueue.add(stringRequest);

            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
    }

    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_PHOTO:
                //postPhoto();
                postStatusUpdate();
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }

    private void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }

    private void postStatusUpdate() {
        Profile profile = Profile.getCurrentProfile();
        String mensajeAlerta;
        if(alerta.getMensaje()==null || alerta.getMensaje().equals("")){
            mensajeAlerta = usuarioFinal+" esta en peligro, contactalo.";
        }else{
            mensajeAlerta = alerta.getMensaje();
        }
       ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(usuarioFinal+ " necesita ayuda, contáctalo.")
                .setContentDescription(mensajeAlerta)
                //.setContentUrl(Uri.parse("https://www.google.com.mx/maps/@22.7611008,-102.5957678,16z"))
                .setContentUrl(Uri.parse("https://www.google.com.mx/maps/place//@"+lat+","+lon+",16z"))
                .setImageUrl(Uri.parse("http://mpcdemexico.com.mx/app/logo_final2.png"))/*.setPlaceId("ChIJOaRYeGpOgoYRJlu99BIKwXU")*///O cuatro.png para pequeña

                //.setContentUrl(Uri.parse("https://www.google.com.mx/maps/place/22.7611178,-102.5976124,17z"))
                .build();


        if (canPresentShareDialog) {
           // shareDialog.show(linkContent);
            ShareApi.share(linkContent, shareCallback);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    private void performPublish(PendingAction action, boolean allowNoToken) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null || allowNoToken) {
            pendingAction = action;
            handlePendingAction();
        }
    }


}
