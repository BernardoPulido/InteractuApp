package com.example.bernardo.seguridadpersonal.Interactions;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bernardo.seguridadpersonal.PruebaDeLogueo;
import com.example.bernardo.seguridadpersonal.R;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;
import com.pushbots.push.Pushbots;

import java.util.HashMap;
import java.util.Map;

public class Watson extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                watson();
            }
        });
    }

    private void watson() {

            Thread thread = new Thread(new Runnable(){
                public void run() {
                    try {
                        NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                                "2018-03-19",
                                "96a482dc-10cd-46a3-860a-1855e4baf1cf",
                                "iEfTDLnp3rEQ"
                        );

                        String text = "Hola mi nombre es Bernardo, hace muchos años comence a desarrollar aplicaciones en esta plataforma y me sorprendió la cantidad de herrameintas que se proveen para que los desarrolladores puedan ampliar su imaginación y generar nuevas y utiles herrmaientas para la vida cotidiana.";
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
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }
    }


