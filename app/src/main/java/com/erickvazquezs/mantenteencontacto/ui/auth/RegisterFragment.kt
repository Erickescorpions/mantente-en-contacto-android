package com.erickvazquezs.mantenteencontacto.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentRegisterBinding
import com.erickvazquezs.mantenteencontacto.models.AvatarEntity
import com.erickvazquezs.mantenteencontacto.models.UserEntity

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val errors = mutableListOf<String>()
    private var avatar: AvatarEntity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgAvatar.setImageResource(R.drawable.img1)

//        binding.imgAvatar.setOnClickListener {
//            val intent = Intent(this, ChooseAvatarActivity::class.java)
//            register.launch(intent)
//        }

        binding.btnCreate.setOnClickListener {
            if(!validate()) {
                for(error in errors) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
                }

                return@setOnClickListener
            }

            val user = UserEntity(
                binding.etUsername.text.toString().trim(),
                binding.etPhoneNumber.text.toString().trim(),
                avatar ?: AvatarEntity(R.drawable.img1),
                binding.etPassword.text.toString()
            )

//            val intent = Intent(this, HomeActivity::class.java).apply {
//                putExtra("EXTRA_USER_KEY", user)
//            }

//            startActivity(intent)
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(activity, R.string.coming_soon, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun validate(): Boolean {
        errors.clear()

        if (binding.etUsername.text.isNullOrEmpty()) {
            errors.add(getString(R.string.error_username_required))
        }

        if (binding.etPhoneNumber.text.isNullOrEmpty()) {
            errors.add(getString(R.string.error_phone_required))
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