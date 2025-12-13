package com.erickvazquezs.mantenteencontacto.utils.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.erickvazquezs.mantenteencontacto.models.PlaceDto
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.erickvazquezs.mantenteencontacto.utils.notifications.NotificationHelper
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

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
        // TODO: Crear notificacion y obtener datos del lugar desde Firestore
        val db = Firebase.firestore

        db.collection("places").document(placeId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val place = documentSnapshot.toObject(PlaceDto::class.java)
                    val placeName = place?.name ?: "Lugar Desconocido"

                    NotificationHelper.showGeofenceNotification(context, placeName, transitionType, placeId)
                } else {
                    Log.e(Constants.LOGTAG, "Error al obtener el lugar")
                }
            }.addOnFailureListener { e ->
                Log.e(Constants.LOGTAG, "Error al obtener el lugar: ${e.message}", e)
                NotificationHelper.showGeofenceNotification(context, "Lugar (Error BD)", transitionType, placeId)
            }
    }
}