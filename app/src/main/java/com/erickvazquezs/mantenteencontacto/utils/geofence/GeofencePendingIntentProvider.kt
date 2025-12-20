package com.erickvazquezs.mantenteencontacto.utils.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object GeofencePendingIntentProvider {
    private const val REQUEST_CODE = 1001
    private var pendingIntent: PendingIntent? = null

    fun get(context: Context): PendingIntent {
        if (pendingIntent != null) return pendingIntent!!

        val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            flags
        )

        return pendingIntent!!
    }
}