package com.erickvazquezs.mantenteencontacto.utils.notifications

import android.util.Log
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

object TokenManager {
    private var cachedToken: String? = null

    fun cacheToken(token: String) {
        cachedToken = token
        Log.d(Constants.LOGTAG, "Token cacheado")
    }

    fun trySaveToken() {
        val token = cachedToken ?: return
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        Firebase.firestore
            .collection("users")
            .document(uid)
            .update("fcmToken", token)
            .addOnSuccessListener {
                Log.d(Constants.LOGTAG, "Token guardado correctamente")
            }
            .addOnFailureListener { e ->
                Log.e(Constants.LOGTAG, "Error guardando token", e)
            }
    }
}
