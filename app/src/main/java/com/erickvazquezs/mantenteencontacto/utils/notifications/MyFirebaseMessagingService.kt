package com.erickvazquezs.mantenteencontacto.utils.notifications

import android.util.Log
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(Constants.LOGTAG, "onNewToken: $token")

        TokenManager.cacheToken(token)
        TokenManager.trySaveToken()
    }
}