package com.example.bernardo.seguridadpersonal.Configuraciones;

/**
 * Created by Bernardo on 19/08/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bernardo.seguridadpersonal.PantallaPrincipal.MapsActivity;
import com.pushbots.push.PBNotificationIntent;
import com.pushbots.push.Pushbots;
import com.pushbots.push.utils.PBConstants;

import java.util.HashMap;
public class customHandler extends BroadcastReceiver
{
    private static final String TAG = "customHandler";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        Log.d(TAG, "action=" + action);
        // Handle Push Message when opened
        if (action.equals(PBConstants.EVENT_MSG_OPEN)) {
            //Check for Pushbots Instance
            Pushbots pushInstance = Pushbots.sharedInstance();
            if(!pushInstance.isInitialized()){
                Pushbots.sharedInstance().init(context.getApplicationContext());
            }

            //Clear Notification array
            if(PBNotificationIntent.notificationsArray != null){
                PBNotificationIntent.notificationsArray = null;
            }

            HashMap<?, ?> PushdataOpen = (HashMap<?, ?>) intent.getExtras().get(PBConstants.EVENT_MSG_OPEN);
            Log.w(TAG, "User clicked notification with Message: " + PushdataOpen.get("message"));

            //Report Opened Push Notification to Pushbots
            if(Pushbots.sharedInstance().isAnalyticsEnabled()){
                Pushbots.sharedInstance().reportPushOpened( (String) PushdataOpen.get("PUSHANALYTICS"));
            }

            //Start lanuch Activity
            String packageName = context.getPackageName();
            Log.d("Paquete", packageName);
            //Intent resultIntent = new Intent(context.getPackageManager().getLaunchIntentForPackage(packageName));
            Intent resultIntent = new Intent(context, MapsActivity.class);
            Log.d("Paquete", resultIntent+"");
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

            resultIntent.putExtras(intent.getBundleExtra("pushData"));
            resultIntent.putExtra("comepush", 1);
            Pushbots.sharedInstance().startActivity(resultIntent);

            // Handle Push Message when received
        }else if(action.equals(PBConstants.EVENT_MSG_RECEIVE)){
            HashMap<?, ?> PushdataOpen = (HashMap<?, ?>) intent.getExtras().get(PBConstants.EVENT_MSG_RECEIVE);
            Log.w(TAG, "User Received notification with Message: " + PushdataOpen.get("message"));
        }
    }
}
