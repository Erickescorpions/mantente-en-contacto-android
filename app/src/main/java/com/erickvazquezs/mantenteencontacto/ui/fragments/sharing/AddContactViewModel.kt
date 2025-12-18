package com.erickvazquezs.mantenteencontacto.ui.fragments.sharing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.erickvazquezs.mantenteencontacto.models.UserDto
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddContactViewModelFactory(private val db: FirebaseFirestore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddContactViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AddContactViewModel(private val db: FirebaseFirestore): ViewModel() {
    private val _usersFound = MutableLiveData<List<UserDto>>()
    val usersFound: LiveData<List<UserDto>> = _usersFound

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun searchUsers(query: String) {
        if (query.length < 2) {
            _usersFound.value = emptyList()
            return
        }

        _isLoading.value = true

        val currentUser = Firebase.auth.currentUser
        val currentUserId = currentUser?.uid ?: return
        var currentFriends = emptyList<String>()
        db.collection("users").document(currentUserId).get()
            .addOnSuccessListener { currentUserSnapshot ->
                currentFriends = currentUserSnapshot.get("friends") as? List<String> ?: emptyList()
            }.addOnFailureListener {
                _isLoading.value = false
                _usersFound.value = emptyList()
            }

        db.collection("users")
            .whereGreaterThanOrEqualTo("username", query)
            .whereLessThanOrEqualTo("username", query + '\uf8ff')
            .limit(10)
            .get()
            .addOnSuccessListener { results ->
                _isLoading.value = false
                val usersList = results.documents.mapNotNull { document ->
                        document.toObject(UserDto::class.java)?.apply {
                            id = document.id
                            isFriend = currentFriends.contains(id)
                        }
                    }.filter { user ->
                        user.id != currentUserId
                    }

                _usersFound.value = usersList
            }
            .addOnFailureListener {
                _isLoading.value = false
                _usersFound.value = emptyList()
            }
    }
}