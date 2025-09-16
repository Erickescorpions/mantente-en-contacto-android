package com.erickvazquezs.mantenteencontacto.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.avatar.ChooseAvatarActivity
import com.erickvazquezs.mantenteencontacto.databinding.ActivityRegisterBinding
import com.erickvazquezs.mantenteencontacto.models.AvatarEntity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    val errors = mutableListOf<String>()

    private val register =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val avatar = result.data?.getSerializableExtra("EXTRA_AVATAR_KEY") as AvatarEntity
                binding.imgAvatar.setImageResource(avatar.avatarId)
            } else {
                Toast.makeText(this, "RESULT_CANCELLED", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // por defecto la imagen sera img1
        binding.imgAvatar.setImageResource(R.drawable.img1)

        binding.imgAvatar.setOnClickListener {
            val intent = Intent(this, ChooseAvatarActivity::class.java)
            register.launch(intent)
        }

//        binding.btnCreate.setOnClickListener {
//            if(!validate()) {
//                // mostramos los errores en la lista
//
//
//                return
//            }
//
//            // si se valida la informacion
//            val intent = Intent(this, )
//        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validate(): Boolean {
        return true
    }
}