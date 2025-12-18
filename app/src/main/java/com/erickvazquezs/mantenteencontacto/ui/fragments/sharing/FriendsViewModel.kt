package com.erickvazquezs.mantenteencontacto.ui.fragments.sharing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.erickvazquezs.mantenteencontacto.models.UserDto
import com.google.firebase.firestore.FirebaseFirestore

class FriendsViewModelFactory(private val db: FirebaseFirestore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendsViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class FriendsViewModel(
    private val db: FirebaseFirestore
): ViewModel() {
    private val _friends = MutableLiveData<List<UserDto>>()
    val friends: LiveData<List<UserDto>> = _friends

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getFriends(userId: String) {
        _isLoading.value = true

        db.collection("users").document(userId).get()
            .addOnSuccessListener { userSnapshot ->

                val friendIds = userSnapshot.get("friends") as? List<String> ?: emptyList()

                if (friendIds.isEmpty()) {
                    _friends.value = emptyList()
                    _isLoading.value = false
                    return@addOnSuccessListener
                }

                val friendsList = mutableListOf<UserDto>()

                friendIds.forEach { friendId ->
                    db.collection("users").document(friendId).get()
                        .addOnSuccessListener { friendSnapshot ->
                            val friend = friendSnapshot.toObject(UserDto::class.java)
                            friend?.id = friendSnapshot.id
                            friend?.let { friendsList.add(it) }

                            if (friendsList.size == friendIds.size) {
                                _friends.value = friendsList
                                _isLoading.value = false
                            }
                        }
                        .addOnFailureListener {
                            _isLoading.value = false
                        }
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }
}