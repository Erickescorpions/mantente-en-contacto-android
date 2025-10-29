package com.erickvazquezs.mantenteencontacto.ui.fragments.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.erickvazquezs.mantenteencontacto.Extensions.dataStore
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentRegisterBinding
import com.erickvazquezs.mantenteencontacto.models.AvatarEntity
import com.erickvazquezs.mantenteencontacto.models.UserEntity
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

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

        val args: RegisterFragmentArgs by navArgs()
        avatar = args.avatar

        if (avatar == null) {
            avatar = AvatarEntity(R.drawable.img1)
        }

        binding.imgAvatar.setImageResource(avatar!!.avatarId)

        binding.imgAvatar.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_chooseAvatarFragment)
        }

        binding.btnCreate.setOnClickListener {
            if (!validate()) {
                Toast.makeText(requireContext(), errors.joinToString("\n"), Toast.LENGTH_SHORT)
                    .show()
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
                        val uid = task.result?.user?.uid
                        if (uid == null) {
                            binding.btnCreate.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                "No se pudo obtener el UID",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@addOnCompleteListener
                        }

                        val db = Firebase.firestore
                        val userMap = hashMapOf(
                            "username" to user.username,
                            "email" to user.email,
                            "avatar" to user.avatar.avatarId // se guarda como número
                        )

                        db.collection("users")
                            .document(uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                setOnboardingCompletedDS()
                                findNavController().navigate(R.id.action_registerFragment_to_userAccountFragment)
                            }
                            .addOnFailureListener { e ->
                                binding.btnCreate.isEnabled = true
                                Log.w(Constants.LOGTAG, "Error guardando usuario", e)
                                Toast.makeText(
                                    requireContext(),
                                    "Error guardando datos, intenta de nuevo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validate(): Boolean {
        errors.clear()

        if (binding.etUsername.text.isNullOrEmpty()) {
            errors.add(getString(R.string.error_username_required))
        }

        val email = binding.etEmail.text

        if (email.isNullOrEmpty()) {
            errors.add(getString(R.string.error_email_required))
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors.add(getString(R.string.error_email_invalid))
        }

        if (binding.etPassword.text.isNullOrEmpty()) {
            errors.add(getString(R.string.error_password_required))
        }

        if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
            errors.add(getString(R.string.error_password_mismatch))
        }

        return errors.isEmpty()
    }

    private fun setOnboardingCompletedDS() {
        viewLifecycleOwner.lifecycleScope.launch {
            requireContext().dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(Constants.ONBOARDING)] = true
            }
        }
    }

}