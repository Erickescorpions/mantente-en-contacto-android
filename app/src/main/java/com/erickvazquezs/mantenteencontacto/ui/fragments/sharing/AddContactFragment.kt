package com.erickvazquezs.mantenteencontacto.ui.fragments.sharing

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentAddContactBinding
import com.erickvazquezs.mantenteencontacto.ui.adapters.sharing.UserSearchAdapter
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddContactFragment : Fragment() {
    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: AddContactViewModel
    private var searchJob: Job? = null
    private var currentFriends = emptyList<String>()
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore

        val factory = AddContactViewModelFactory(db)
        viewModel = ViewModelProvider(this, factory).get(AddContactViewModel::class.java)
        loadCurrentFriends()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore

        currentUserId = Firebase.auth.uid
        if (currentUserId == null) {
            return
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.etUsername.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(300L)

                if (query.length > 1) {
                    viewModel.searchUsers(query, currentUserId!!, currentFriends)
                } else {
                    viewModel.searchUsers("", currentUserId!!, currentFriends)
                }
            }
        }

        viewModel.usersFound.observe(viewLifecycleOwner) { users ->
            binding.rvUsersFound.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = UserSearchAdapter(users) { selectedFriend ->
                    addFriend(Firebase.auth.currentUser?.uid!!, selectedFriend.id!!) { success: Boolean ->
                        if (success) {
                            Toast.makeText(context, "Amigo agregado", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        } else {
                            Toast.makeText(context, "Error al agregar amigo", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addFriend(userId: String, friendId: String, onComplete: ((Boolean) -> Unit)? = null) {
        val batch = db.batch()
        val userRef = db.collection("users").document(userId)
        val friendRef = db.collection("users").document(friendId)

        batch.update(userRef, "friends", FieldValue.arrayUnion(friendId))
        batch.update(friendRef, "friends", FieldValue.arrayUnion(userId))

        batch.commit()
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener {
                it.printStackTrace()
                onComplete?.invoke(false)
            }
    }

    private fun loadCurrentFriends() {
        val currentUserId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users").document(currentUserId).get()
            .addOnSuccessListener { snapshot ->
                currentFriends = snapshot.get("friends") as? List<String> ?: emptyList()
            }
            .addOnFailureListener {
                currentFriends = emptyList()
            }
    }


}