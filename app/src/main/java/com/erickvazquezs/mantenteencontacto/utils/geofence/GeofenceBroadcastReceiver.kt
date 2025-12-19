package com.erickvazquezs.mantenteencontacto.utils.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.functions

class GeofenceBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent?.hasError() == true) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(Constants.LOGTAG, "Error en la Geocerca (Broadcast): $errorMessage")
            return
        }
        val geofenceTransition = geofencingEvent?.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            // request id es el id del place guardado en firebase
            val geofenceRequestIds = triggeringGeofences?.map { it.requestId }

            val transitionString = when (geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> "Entrada"
                Geofence.GEOFENCE_TRANSITION_EXIT -> "Salida"
                else -> "Desconocida"
            }

            triggeringGeofences?.forEach { geofence ->
                val placeId = geofence.requestId
                sendNotification(context, transitionString, placeId)
            }
        }
    }

    private fun sendNotification(context: Context, transitionType: String, placeId: String) {
        if (transitionType != "Entrada") return

        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser ?: run {
            Log.e(Constants.LOGTAG, "Usuario no autenticado")
            return
        }

        Log.d(Constants.LOGTAG, "Llamando notifyArrival para placeId=$placeId")

        Firebase.functions
            .getHttpsCallable("notifyArrival")
            .call(mapOf("placeId" to placeId))
            .addOnSuccessListener { result ->
                Log.d(
                    Constants.LOGTAG,
                    "notifyArrival OK: ${result.data}"
                )
            }
            .addOnFailureListener { e ->
                Log.e(
                    Constants.LOGTAG,
                    "Error llamando notifyArrival",
                    e
                )
            }
    }
}