package com.erickvazquezs.mantenteencontacto.ui.fragments.locations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.erickvazquezs.mantenteencontacto.models.PlaceDto
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class MapsViewModel: ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var placesListener: ListenerRegistration? = null

    private val _registeredPlaces = MutableLiveData<List<PlaceDto>>()
    val registeredPlaces: LiveData<List<PlaceDto>> = _registeredPlaces

    fun startSubscription() {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            _registeredPlaces.value = emptyList()
            return
        }

        if (placesListener == null) {
            placesListener = db.collection("places")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e(Constants.LOGTAG, "Error escuchando lugares:", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val placesList = snapshot.documents.mapNotNull { document ->
                            val place = document.toObject(PlaceDto::class.java)
                            place?.id = document.id
                            place
                        }

                        _registeredPlaces.value = placesList
                        Log.d(Constants.LOGTAG, "Lugares actualizados. Total: ${placesList.size}")
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        placesListener?.remove()
        placesListener = null
    }
}