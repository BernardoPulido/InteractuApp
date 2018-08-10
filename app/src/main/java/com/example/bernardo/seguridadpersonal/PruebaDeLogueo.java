package com.example.bernardo.seguridadpersonal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bernardo.seguridadpersonal.Registro.RecuperacionPass;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.pushbots.push.Pushbots;


public class PruebaDeLogueo extends AppCompatActivity implements View.OnClickListener{

    private TextView info;
    private CallbackManager callbackManager;
    public Profile profile;


    public static final String LOGIN_URL = "http://www.mpcdemexico.com.mx/app/Login.php";

    public static final String KEY_USERNAME="username";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_TOKEN="token";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;

    private String username;
    private String password;
    private TextView olvidaste;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_prueba_de_logueo);

        editTextUsername = (EditText) findViewById(R.id.userText);
        editTextPassword = (EditText) findViewById(R.id.passText);
        olvidaste = (TextView)findViewById(R.id.olvidaste);
        olvidaste.setOnClickListener(this);


        buttonLogin = (Button) findViewById(R.id.login);


        buttonLogin.setOnClickListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        info = (TextView)findViewById(R.id.info);

    }



    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void userLogin() {
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        final String token = Pushbots.sharedInstance().regID();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            openProfile();
                        }else{
                            Toast.makeText(PruebaDeLogueo.this,response,Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PruebaDeLogueo.this,"¡Revisa tu conexión a internet!",Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put(KEY_USERNAME,username);
                map.put(KEY_PASSWORD,password);
                map.put(KEY_TOKEN,token);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void openProfile(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(KEY_USERNAME, username);
        intent.putExtra("inicial", 1);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.olvidaste){
            Intent inte = new Intent(this, RecuperacionPass.class);
            startActivity(inte);
        }else{
            userLogin();
        }


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        float fbIconScale = 1.45F;
        Drawable drawable = this.getResources().getDrawable(
                com.facebook.R.drawable.com_facebook_button_icon);
        drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*fbIconScale),
                (int)(drawable.getIntrinsicHeight()*fbIconScale));



    }
}
