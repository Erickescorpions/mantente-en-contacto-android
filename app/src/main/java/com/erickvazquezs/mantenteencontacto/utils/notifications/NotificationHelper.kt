package com.erickvazquezs.mantenteencontacto.utils.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.erickvazquezs.mantenteencontacto.R

object NotificationHelper {
    private const val CHANNEL_ID = "GEOFENCE_ALERTS_CHANNEL"
    private const val CHANNEL_NAME = "Alertas de Geocerca"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // Alerta importante
            ).apply {
                description = "Notificaciones de entrada y salida de lugares registrados."
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showGeofenceNotification(context: Context, placeName: String, transitionType: String, placeId: String) {

        //  al hacer click en la notificacion
        val fullScreenIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            placeId.hashCode(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "$transitionType de Lugar"
        val message = "¡Has ${if (transitionType == "Entrada") "llegado a" else "salido de"} $placeName!"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(placeId.hashCode(), builder.build())
    }
}