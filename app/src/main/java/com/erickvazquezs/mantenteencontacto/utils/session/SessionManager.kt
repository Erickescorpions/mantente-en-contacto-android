package com.erickvazquezs.mantenteencontacto.utils.session

import android.content.Context
import android.util.Log
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.erickvazquezs.mantenteencontacto.utils.geofence.GeofenceManager
import com.google.firebase.auth.FirebaseAuth

object SessionManager {
    fun logout(context: Context, onComplete: () -> Unit) {
        Log.d(Constants.LOGTAG, "Cerrando sesión")

        FirebaseAuth.getInstance().signOut()
        GeofenceManager.removeAll(context)

        onComplete()
    }
}