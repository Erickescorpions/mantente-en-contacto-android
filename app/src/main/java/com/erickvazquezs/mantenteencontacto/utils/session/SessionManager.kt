package com.erickvazquezs.mantenteencontacto.utils.session

import android.util.Log
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.google.firebase.auth.FirebaseAuth

object SessionManager {
    fun logout(onComplete: () -> Unit) {
        Log.d(Constants.LOGTAG, "Cerrando sesión")

        FirebaseAuth.getInstance().signOut()
        onComplete()
    }
}