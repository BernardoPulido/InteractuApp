package com.example.bernardo.seguridadpersonal.Configuraciones;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.bernardo.seguridadpersonal.Acciones;
import com.example.bernardo.seguridadpersonal.MainActivity;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import db.Alertas;
import db.HistorialAlertas;
import db.Parametros;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

/**
 * Created by Bernardo on 19/09/2016.
 */
public class Reconocimiento extends Service implements edu.cmu.pocketsphinx.RecognitionListener{

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String PHONE_SEARCH = "phones";
    private static final String MENU_SEARCH = "menu";
    List<String> pruebas = new ArrayList<String>();
    List<Integer> identificadores = new ArrayList<Integer>();

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "mi nombre";
    private int indiceGeneral =0;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;
    private boolean banderaGeneral=true;

    final static String ACTION = "NotifyServiceAction";
    final static String STOP_SERVICE = "";
    final static int RQS_STOP_SERVICE = 1;
    NotifyServiceReceiver notifyServiceReceiver;

    @Override
    public void onCreate() {
// TODO Auto-generated method stub
        /*notifyServiceReceiver = new NotifyServiceReceiver();
        super.onCreate();*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
            //Receiver for stop
           /* IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION);
            registerReceiver(notifyServiceReceiver, intentFilter);

            banderaGeneral = false;

            //Actualizar arrayLists
            db.DateBaseHelper helperHistorialeliminar = new db.DateBaseHelper(Reconocimiento.this);
            RuntimeExceptionDao<Alertas, Integer> daoAlertas = helperHistorialeliminar.getDaoREalertas();

            List<Alertas> listaDeAlertas = daoAlertas.queryForAll();
            helperHistorialeliminar.close();
            pruebas.clear();
            identificadores.clear();

            for (Alertas alerta : listaDeAlertas) {
                if (!alerta.getPatronDetonante().equals("")) {
                    pruebas.add(alerta.getPatronDetonante().toLowerCase());
                    identificadores.add(alerta.getIdAlerta());
                }
            }

            if (intent != null) {
                if (intent.getIntExtra("bandera", 0) == 1) {
                    try {
                        writeKeywordFile(pruebas, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (verificarArchivo() == this.indiceGeneral) {
                Log.d("Si", "Sobreescribiendo");
                try {
                    writeKeywordFile(pruebas, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Intentemos();
            if (intent == null) {
                //if(listaDeAlertas.size()!=0) {
                    NotificacionActivado.notify(this, "", 1);
                //}
            }


        return super.onStartCommand(intent, flags, startId);*/
           return 0;

    }

    public void verificarArchivoGeneral(){
        //banderaGeneral=false;

        if(verificarArchivo()==this.indiceGeneral){
            Log.d("Si", "Sobreescribiendo");
            //Actualizar arrayLists
            db.DateBaseHelper helperHistorialeliminar2 = new db.DateBaseHelper(Reconocimiento.this);
            RuntimeExceptionDao<Alertas, Integer> daoAlertas2 = helperHistorialeliminar2.getDaoREalertas();

            List<Alertas> listaDeAlertas = daoAlertas2.queryForAll();
            helperHistorialeliminar2.close();
            pruebas.clear();
            identificadores.clear();

            for (Alertas alerta:listaDeAlertas) {
                if(!alerta.getPatronDetonante().equals("")) {
                    pruebas.add(alerta.getPatronDetonante().toLowerCase());
                    identificadores.add(alerta.getIdAlerta());
                }
            }

            try {
                writeKeywordFile(pruebas, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void Intentemos(){
        runRecognizerSetup();
        leerarchivo();
    }

    private int verificarArchivo(){
        int i=0;
        try {
            String cadena;
            Assets assets = new Assets(this);
            File assetDir = assets.syncAssets();
            File file = new File(assetDir, "menu.gram");
            FileReader f = new FileReader(file);
            BufferedReader b = new BufferedReader(f);
            while ((cadena = b.readLine()) != null) {
                i++;
            }
            b.close();
        }catch (IOException e){

        }
        return i;
    }
    private void leerarchivo() {
        try {
            String cadena;
            Assets assets = new Assets(this);
            File assetDir = assets.syncAssets();
            File file = new File(assetDir, "menu.gram");
            FileReader f = new FileReader(file);
            BufferedReader b = new BufferedReader(f);
            int i=1;
            while ((cadena = b.readLine()) != null) {
                Log.d("Linea "+i, cadena);
                i++;
            }
            b.close();
        }catch (IOException e){

        }
    }




    public void writeKeywordFile(List<String> keywords, Context context) throws Exception {

        Assets assets = new Assets(this);
        File assetDir = assets.syncAssets();
        File file = new File(assetDir, "menu.gram");

        try{
            FileWriter w = new FileWriter(file);

            BufferedWriter bw = new BufferedWriter(w);
            PrintWriter wr = new PrintWriter(bw);
            for(String keyword : keywords){
                wr.write(keyword+"/1e-50/");
                wr.write("\n");
            }
            wr.close();
            bw.close();

        }catch(IOException e){};

    }
    private void runRecognizerSetup() {
        /*new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(Reconocimiento.this);
                    File assetDir = assets.syncAssets();
                    verificarArchivoGeneral(); //Prueba para verificar errores
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Log.d("Problema","Failed to init recognizer " + result.getMessage());
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();*/
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            //recognizer.shutdown();
        }
       // this.unregisterReceiver(notifyServiceReceiver);
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {

        /*if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        Log.d("Parcial", text+" "+banderaGeneral);
        if(!banderaGeneral){
            //Log.d("Entro", "bandera");
            int aux =0;
            for (int i=0; i<pruebas.size();i++) {
                if(pruebas.get(i).equals(text)){
                    aux = identificadores.get(i);
                }
            }
            //Log.d("Entro", "aux="+aux);

            if(aux!=0){
                Intent intent = new Intent(this, Acciones.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Revisar bien si el cambio de bandera sigue funcionando
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Revisar bien si el cambio de bandera sigue funcionando
                intent.putExtra("id", aux);
                intent.putExtra("notify", 1);
                startActivity(intent);
                //hypothesis.delete();
                Log.d("Id enviado", aux+"");
            }
            banderaGeneral=true;
        }
        //hypothesis.delete();

        if (text.equals(KEYPHRASE))
            switchSearch(MENU_SEARCH);
        else{
            //Log.d("Ninguna", text);
        }
*/
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {

        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            Log.d("Resultado", text);
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();
        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);

        //String caption = getResources().getString(captions.get(searchName));

    }

    private void reset()
    {
        recognizer.stop();
        //recognizer.startListening(KWS_SEARCH);
    }
    private void setupRecognizer(File assetsDir) throws IOException {

        /*recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "es.dict"))
                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setKeywordThreshold(1e-45f) // Threshold to tune for keyphrase to balance between false alarms and misses
                .setBoolean("-allphone_ci", true)  // Use context-independent phonetic search, context-dependent is too slow for mobile


                .getRecognizer();
        recognizer.addListener(this);

        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addKeywordSearch(KWS_SEARCH, menuGrammar);*/

    }


    @Override
    public void onError(Exception error) {
        Log.d("Error", error.getMessage()+"");
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }


    public class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            int rqs = arg1.getIntExtra("RQS", 0);
            if (rqs == RQS_STOP_SERVICE){
                NotificacionActivado.cancel(Reconocimiento.this);
                stopSelf();
            }
        }
    }


}
