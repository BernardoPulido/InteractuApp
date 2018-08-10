package com.example.bernardo.seguridadpersonal.Registro;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bernardo.seguridadpersonal.MainActivity;
import com.example.bernardo.seguridadpersonal.PruebaDeLogueo;
import com.example.bernardo.seguridadpersonal.R;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity implements View.OnClickListener {

    private static final String REGISTER_URL = "http://mpcdemexico.com.mx/app/volleyRegister.php";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_APELLIDO = "apellido";
    public static final String KEY_EDAD = "edad";

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText confirmacion;
    private EditText Nombre;
    private EditText Apellido;
    private EditText Edad;


    private Button buttonRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextUsername = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.pass);
        editTextEmail= (EditText) findViewById(R.id.correo);
        confirmacion = (EditText) findViewById(R.id.pass2);

        Nombre = (EditText) findViewById(R.id.nombre);
        Apellido = (EditText) findViewById(R.id.apellido);
        Edad= (EditText) findViewById(R.id.edad);

        buttonRegister = (Button) findViewById(R.id.register);
        buttonRegister.setOnClickListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonRegister.performClick();
            }
        });
    }

    public void dormir(){
        SystemClock.sleep(800);
        Intent intent = new Intent(this, PruebaDeLogueo.class);

        startActivity(intent);
    }


    public void registrar(){
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        final String nombre = Nombre.getText().toString().trim();
        final String apellido = Apellido.getText().toString().trim();
        final String edad = Edad.getText().toString().trim();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Successfully Registered")){
                            Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_LONG).show();
                            dormir();
                        }else{
                            Toast.makeText(Registro.this, response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Registro.this,"¡Revisa tu conexión a internet!",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_USERNAME,username);
                params.put(KEY_PASSWORD,password);
                params.put(KEY_EMAIL, email);
                params.put(KEY_NOMBRE, nombre);
                params.put(KEY_APELLIDO, apellido);
                params.put(KEY_EDAD, edad);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {

        if(confirmacion.getText().toString().equals(editTextPassword.getText().toString())){
            if(TextUtils.isEmpty(editTextEmail.getText().toString()) || TextUtils.isEmpty(editTextUsername.getText().toString()) || TextUtils.isEmpty(editTextPassword.getText().toString()) || TextUtils.isEmpty(Nombre.getText().toString()) || TextUtils.isEmpty(Apellido.getText().toString()) || TextUtils.isEmpty(Edad.getText().toString())) {
                Toast.makeText(Registro.this,"Faltan campos por llenar",Toast.LENGTH_LONG).show();
            }else{
                //Toast.makeText(Registro.this,Nombre.getText().toString().trim()+" "+Apellido.getText().toString().trim()+" "+Edad.getText().toString().trim()+" "+editTextEmail.getText().toString().trim()+" "+editTextUsername.getText().toString()+" "+editTextPassword.getText().toString()+" "+confirmacion.getText().toString(),Toast.LENGTH_LONG).show();
                registrar();
            }
        }else{
            Toast.makeText(Registro.this,"Contraseñas no coinciden",Toast.LENGTH_LONG).show();
        }

    }
}
