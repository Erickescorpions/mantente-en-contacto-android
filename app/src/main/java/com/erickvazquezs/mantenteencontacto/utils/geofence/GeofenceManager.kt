package com.erickvazquezs.mantenteencontacto.utils.geofence

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.erickvazquezs.mantenteencontacto.models.PlaceDto
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

object GeofenceManager {
    @SuppressLint("MissingPermission")
    fun add(context: Context, place: PlaceDto, onResult: (Boolean) -> Unit) {
        val id = place.id ?: run {
            onResult(false)
            return
        }

        val geofence = Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(
                place.latitude,
                place.longitude,
                place.radius.toFloat()
            )
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val client = LocationServices.getGeofencingClient(context)

        client.addGeofences(
            request,
            GeofencePendingIntentProvider.get(context)
        )
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun removeAll(context: Context) {
        val client = LocationServices.getGeofencingClient(context)

        client.removeGeofences(GeofencePendingIntentProvider.get(context))
            .addOnSuccessListener {
                Log.d(Constants.LOGTAG, "Geocercas eliminadas")
            }
            .addOnFailureListener { error ->
                Log.e(Constants.LOGTAG, "Error al eliminar las geocercas", error)
            }
    }
}