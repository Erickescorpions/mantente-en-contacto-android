package com.erickvazquezs.mantenteencontacto.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentRegisterBinding
import com.erickvazquezs.mantenteencontacto.models.AvatarEntity
import com.erickvazquezs.mantenteencontacto.models.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    private val errors = mutableListOf<String>()
    private var avatar: AvatarEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null && findNavController().currentDestination?.id == R.id.registerFragment) {
            findNavController().navigate(R.id.action_registerFragment_to_userAccountFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgAvatar.setImageResource(R.drawable.img1)
        binding.btnCreate.setOnClickListener {
            if (!validate()) {
                Toast.makeText(requireContext(), errors.joinToString("\n"), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnCreate.isEnabled = false

            val user = UserEntity(
                binding.etUsername.text.toString().trim(),
                null,
                binding.etEmail.text.toString().trim(),
                avatar ?: AvatarEntity(R.drawable.img1),
                binding.etPassword.text.toString()
            )

            auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        findNavController().navigate(R.id.action_registerFragment_to_userAccountFragment)
                    } else {
                        binding.btnCreate.isEnabled = true
                        Toast.makeText(requireContext(), "Try again!", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(activity, R.string.coming_soon, Toast.LENGTH_SHORT).show()
        }

    }

    private fun validate(): Boolean {
        errors.clear()

        if (binding.etUsername.text.isNullOrEmpty()) {
            errors.add(getString(R.string.error_username_required))
        }

        if (binding.etEmail.text.isNullOrEmpty()) {
            errors.add(getString(R.string.error_email_required))
        }

        if (binding.etPassword.text.isNullOrEmpty()) {
            errors.add(getString(R.string.error_password_required))
        }

        if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
            errors.add(getString(R.string.error_password_mismatch))
        }

        return errors.isEmpty()
    }
}