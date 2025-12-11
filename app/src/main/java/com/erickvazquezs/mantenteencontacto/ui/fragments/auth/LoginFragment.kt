package com.erickvazquezs.mantenteencontacto.ui.fragments.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // creamos la instancia de firebase
        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (validate(email, password)) return@setOnClickListener
            login(email, password)
        }

        // para navegar a la vista del register
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validate(email: String, password: String): Boolean {
        var errors = false

        // Validación del correo electrónico
        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.error_empty_email)
            errors = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = getString(R.string.error_invalid_email)
            errors = true
        }

        // Validación de la contraseña
        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.error_empty_password)
            errors = true
        } else if (password.length < 6) {
            binding.etPassword.error = getString(R.string.error_weak_password)
            errors = true
        }

        return errors
    }

    private fun login(usr: String, psw: String) {
        auth.signInWithEmailAndPassword(usr, psw)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    actionLoginSuccessful()
                } else {
                    handleErrors(authResult)
                }
            }
    }

    private fun actionLoginSuccessful() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToMoviesListFragment()
        )
    }

    private fun handleErrors(task: Task<AuthResult>) {
        val exception = task.exception

        when (exception) {
            is FirebaseAuthInvalidUserException -> {
                binding.etEmail.error = getString(R.string.error_email_not_in_db)
                binding.etEmail.requestFocus()
            }

            is FirebaseAuthInvalidCredentialsException -> {
                binding.etEmail.error = getString(R.string.error_invalid_credentials)
                binding.etEmail.requestFocus()
                binding.etPassword.setText("")
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