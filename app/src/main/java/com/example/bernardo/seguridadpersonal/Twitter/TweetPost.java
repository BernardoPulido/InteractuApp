package com.example.bernardo.seguridadpersonal.Twitter;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.app.Activity;
import android.os.StrictMode;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.InputStream;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import com.example.bernardo.seguridadpersonal.MainActivity;
import com.example.bernardo.seguridadpersonal.R;

public class TweetPost extends AppCompatActivity {

    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    public static final int WEBVIEW_REQUEST_CODE = 100;

    private static Twitter twitter;
    private static RequestToken requestToken;

    private static SharedPreferences sharedPreferences;

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;

    //Variables publicación
    String lat="23.0727536", lon="-104.7926761";
    String usuarioFinal = "Unknown";
    String mensaje=" necesita ayuda, contactalo.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTwitterConfigs();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_tweet_post);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{

        }

        try{
            usuarioFinal = getIntent().getStringExtra("usuarioFinal");
            lat = getIntent().getStringExtra("lat");
            lon = getIntent().getStringExtra("lon");
        }catch (Exception e){}

        try {
            postInTwitter();
        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

    private void postInTwitter() throws TwitterException {
        final String status = usuarioFinal+mensaje;

        if(status.trim().length() > 0) {
            new updateTwitterStatus().execute(status);

        } else {
            Toast.makeText(this, "Message is empty!!", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, MainActivity.class);
        if(getIntent().getIntExtra("llamada", 0)==1){
            intent.putExtra("llamar", 1);
            intent.putExtra("numero", getIntent().getStringExtra("num"));
        }

        startActivity(intent);
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        final String status = usuarioFinal+mensaje;

        if(status.trim().length() > 0) {
            new updateTwitterStatus().execute(status);
        } else {
            Toast.makeText(this, "Message is empty!!", Toast.LENGTH_SHORT).show();
        }


        Intent intent = new Intent(this, MainActivity.class);
        if(getIntent().getIntExtra("llamada", 0)==1){
            intent.putExtra("llamar", 1);
            intent.putExtra("numero", getIntent().getStringExtra("num"));
        }
        startActivity(intent);
        super.onPostCreate(savedInstanceState, persistentState);
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
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    class updateTwitterStatus extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            String status = params[0];
            try {
                char[] arrayChar = getIntent().getStringExtra("mensaje").toCharArray();

                if (arrayChar.length <= 60 && arrayChar.length > 0) {
                    status = getIntent().getStringExtra("mensaje") + " ";
                }
            }catch (Exception e){}

            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);
                //builder.setOAuthAccessToken("755882624782970880-esOQ5HGcdUYBWl4jb1qdi05m0ykjujA");
                //builder.setOAuthAccessTokenSecret("LlAynbiWjxcSceJAppdgTY7sE1bvXsl9W9v1bJdkNL84c");

                String access_token = sharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                String acces_token_secret = sharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                //Se cambio por lo de arriba.
                //String access_token = "755882624782970880-esOQ5HGcdUYBWl4jb1qdi05m0ykjujA";
                //String acces_token_secret = "LlAynbiWjxcSceJAppdgTY7sE1bvXsl9W9v1bJdkNL84c";

                AccessToken accessToken = new AccessToken(access_token, acces_token_secret);

                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
                String postear = status + Uri.parse("https://www.google.com.mx/maps/place//@"+lat+","+lon+",16z");
                StatusUpdate statusUpdate = new StatusUpdate(postear);
                InputStream is = getResources().openRawResource(+R.mipmap.iconopeqe);
                statusUpdate.setMedia("test.jpg", is);

                twitter4j.Status response = twitter.updateStatus(statusUpdate);

                try{
                    //Mensajes directos
                    //twitter.sendDirectMessage("pulidogaytan", "Seguridad personal");
                   // twitter.sendDirectMessage("sernabla", "Hola soy app");
                }catch (Exception e){

                }


            } catch (TwitterException e) {
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(TweetPost.this, "Posted to Twitter!", Toast.LENGTH_SHORT);
        }
    }

}
