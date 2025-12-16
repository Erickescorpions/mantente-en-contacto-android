package com.erickvazquezs.mantenteencontacto.ui.fragments.sharing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.erickvazquezs.mantenteencontacto.models.UserDto
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
        val lowerCaseQuery = query.lowercase()

        db.collection("users")
            .whereGreaterThanOrEqualTo("username", lowerCaseQuery)
            .whereLessThanOrEqualTo("username", lowerCaseQuery + '\uf8ff')
            .limit(10)
            .get()
            .addOnSuccessListener { results ->
                _isLoading.value = false

                val usersList = results.documents.mapNotNull { document ->
                    document.toObject(UserDto::class.java)
                }
                _usersFound.value = usersList
            }
            .addOnFailureListener {
                _isLoading.value = false
                _usersFound.value = emptyList()
                // TODO: Manejar y registrar el error
            }
    }
}