package com.example.bernardo.seguridadpersonal.Configuraciones;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.bernardo.seguridadpersonal.MainActivity;
import com.example.bernardo.seguridadpersonal.R;

/**
 * Helper class for showing and canceling notificacion activado
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class NotificacionActivado {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "NotificacionActivado";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of notificacion activado notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */
    public static void notify(final Context context,
                              final String exampleString, final int number) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.iconopeqe);


        final String ticker = exampleString;
        final String title = res.getString(
                R.string.notificacion_activado_notification_title_template, exampleString);
        final String text = res.getString(
                R.string.notificacion_activado_notification_placeholder_text_template, exampleString);
        Intent intento = new Intent();
        intento.setAction(Reconocimiento.ACTION);
        intento.putExtra("RQS", Reconocimiento.RQS_STOP_SERVICE);



        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                .setDefaults(Notification.DEFAULT_LIGHTS)

                .setSmallIcon(R.drawable.iconopeqe)
                .setContentTitle(title)
                .setContentText(text)

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(picture)
                .setTicker(ticker)

                .setOngoing(true)
                // Show expanded text content on devices running Android 4.1 or
                // later.
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title))

                .setAutoCancel(false)

                .addAction(
                        R.drawable.ic_action_stat_share,
                        res.getString(R.string.action_share),
                        PendingIntent.getActivity(
                                context,
                                0,
                                Intent.createChooser(new Intent(Intent.ACTION_SEND)
                                        .setType("text/plain")
                                        .putExtra(Intent.EXTRA_TEXT, "Hola te invito a descargar e instalar la aplicación Seguridad Personal, donde podrás alertar y ser alertado ante algún momento de emergencia.\nSigue este link: http://mpcdemexico.com.mx/app/"), "Compartir aplicación Seguridad Personal"),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(
                        R.drawable.ic_not_interested_black_24dp,
                        res.getString(R.string.action_reply),
                        PendingIntent.getBroadcast(context, 1, intento, PendingIntent.FLAG_UPDATE_CURRENT))


                .setAutoCancel(false);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, int)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
