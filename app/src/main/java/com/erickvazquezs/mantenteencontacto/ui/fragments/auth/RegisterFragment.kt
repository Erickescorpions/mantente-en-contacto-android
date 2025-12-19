package com.erickvazquezs.mantenteencontacto.ui.fragments.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentRegisterBinding
import com.erickvazquezs.mantenteencontacto.models.UserDto
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.erickvazquezs.mantenteencontacto.utils.notifications.TokenManager
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.firestore

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {

            binding.btnRegister.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE

            val email = binding.etEmail.text.toString()
            val name = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            val passwordConfirmation = binding.etPasswordConfirmation.text.toString()

            val data = mapOf(
                "email" to email,
                "name" to name,
                "password" to password,
                "passwordConfirmation" to passwordConfirmation
            )

            if (validate(data)) {
                binding.btnRegister.isEnabled = true
                binding.progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            createUser(email, password, name)
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validate(data: Map<String, String>): Boolean {
        var errors = false

        val name = data["name"].orEmpty()
        val email = data["email"].orEmpty()
        val password = data["password"].orEmpty()
        val passwordConfirmation = data["passwordConfirmation"].orEmpty()

        if (name.isEmpty()) {
            binding.etUsername.error = getString(R.string.error_username_required)
            errors = true
        }

        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.error_email_required)
            errors = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = getString(R.string.error_email_invalid)
            errors = true
        }

        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.error_password_required)
            errors = true
        } else if (password.length < 6) {
            binding.etPassword.error = getString(R.string.error_password_weak)
            errors = true
        } else if (passwordConfirmation.isEmpty()) {
            binding.etPasswordConfirmation.error =
                getString(R.string.error_password_confirmation_required)
            errors = true
        } else if (password != passwordConfirmation) {
            binding.etPassword.error = getString(R.string.error_password_mismatch)
            errors = true
        }

        return errors
    }


    private fun actionRegisterSuccessful(user: UserDto) {
        val db = Firebase.firestore
        db.collection("users")
            .document(user.id!!)
            .set(user)
            .addOnSuccessListener { e ->
                // navegamos al home
                Log.d(Constants.LOGTAG, "Usuario ${user.email} guardado exitosamente en Firestore.")
                TokenManager.trySaveToken()

                findNavController().navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToProfilePhotoSelectionFragment()
                )
            }.addOnFailureListener { exception ->
                // manejamos el error aqui
                Log.e(
                    Constants.LOGTAG,
                    "Error al guardar el documento del usuario: ${exception.message}"
                )
            }
    }

    private fun createUser(email: String, psw: String, name: String) {
        auth.createUserWithEmailAndPassword(email, psw)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    val firebaseUser = authResult.result?.user
                    if (firebaseUser != null) {
                        val user = UserDto(
                            username = name,
                            email = email,
                            id = firebaseUser.uid
                        )

                        actionRegisterSuccessful(user)
                    } else {
                        // manejar el caso donde firebaseUser es nulo
                    }
                } else {
                    handleErrors(authResult)
                    binding.btnRegister.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
            }
    }

    private fun handleErrors(task: Task<AuthResult>) {
        val exception = task.exception

        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                binding.etPassword.error =
                    getString(R.string.error_password_weak)
                binding.etPassword.requestFocus()
                binding.etPassword.setText("")
            }

            is FirebaseAuthInvalidCredentialsException -> {
                binding.etEmail.error = getString(R.string.error_email_invalid)
                binding.etEmail.requestFocus()
            }

            is FirebaseAuthUserCollisionException -> {
                binding.etEmail.error = getString(R.string.error_email_already_in_use)
                binding.etEmail.requestFocus()
            }

            else -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_global),
                    Toast.LENGTH_SHORT
                ).show()
                exception?.printStackTrace()
            }
        }
    }

}