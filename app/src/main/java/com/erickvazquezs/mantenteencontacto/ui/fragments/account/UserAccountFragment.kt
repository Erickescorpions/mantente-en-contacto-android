package com.erickvazquezs.mantenteencontacto.ui.fragments.account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentUserAccountBinding
import com.erickvazquezs.mantenteencontacto.models.UserDto
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class UserAccountFragment : Fragment() {

    private var _binding: FragmentUserAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = auth.currentUser
        val db = Firebase.firestore

        if (currentUser != null) {
            val docRef = db.collection("users").document(currentUser.uid)

            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Document found in the offline cache
                    val document = task.result
                    val user = document.toObject(UserDto::class.java)

                    if (user != null) {
                        binding.ivAvatar.setImageResource(user.avatar)
                        binding.tvUsername.text = user.username
                    }
                } else {
                    Log.d(Constants.LOGTAG, "Error al traer informacion del usuario: ", task.exception)
                }
            }
        } else {
            // no esta autenticado, lo mandamos al onboarding
            findNavController().navigate(R.id.action_userAccountFragment_to_mainOnboardingFragment2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}