package com.example.bernardo.seguridadpersonal.Registro;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bernardo.seguridadpersonal.MainActivity;
import com.example.bernardo.seguridadpersonal.PruebaDeLogueo;
import com.example.bernardo.seguridadpersonal.R;
import com.pushbots.push.Pushbots;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecuperacionPass extends AppCompatActivity implements View.OnClickListener{
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    TextInputLayout email;
    public static final String PETICION_URL = "http://www.mpcdemexico.com.mx/app/Recuperar_Pass.php";

    EditText emailContent;
    Button botonrec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion_pass);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarrec);
        setSupportActionBar(toolbar);

       email = (TextInputLayout) findViewById(R.id.recpass);
        emailContent = (EditText)findViewById(R.id.emailRec);
        botonrec = (Button)findViewById(R.id.enterrecpasss);
        botonrec.setOnClickListener(this);

        email.setHint("Email");


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecuperacionPass.this, PruebaDeLogueo.class);
                startActivity(intent);

            }
        });
    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @Override
    public void onClick(View v) {
        if(botonrec.getText().equals("Regresar")){
            Intent inte = new Intent(this, PruebaDeLogueo.class);
            startActivity(inte);
        }else {
            hideKeyboard();
            String username = email.getEditText().getText().toString();
            if (!validateEmail(username)) {
                email.setError("¡Email inválido! Favor de verificar");
            } else {
                email.setErrorEnabled(false);
                peticion(username);
            }
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void peticion(String valor) {

        final String correo = valor;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PETICION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String res="";
                        if(response.equals("FB")){
                            res="Correo no registrado";
                        }else if (response.equals("ERROR")){
                            res="Intentalo nuevamente";
                        }else if(response.equals("OK")){
                            res="Revisa tu bandeja de entrada, enviamos un email.";
                            botonrec.setText("Regresar");
                        }else{
                            res=response;
                        }
                        Toast.makeText(RecuperacionPass.this, res, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RecuperacionPass.this,"¡Revisa tu conexión a internet!",Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("correo",correo);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
