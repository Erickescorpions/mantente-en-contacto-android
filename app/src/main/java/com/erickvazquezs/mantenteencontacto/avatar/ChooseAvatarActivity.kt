package com.erickvazquezs.mantenteencontacto.avatar

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.ActivityChooseAvatarBinding
import com.erickvazquezs.mantenteencontacto.models.AvatarEntity

class ChooseAvatarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseAvatarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityChooseAvatarBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val data = listOf(
            AvatarEntity(R.drawable.img1),
            AvatarEntity(R.drawable.img2),
            AvatarEntity(R.drawable.img3),
            AvatarEntity(R.drawable.img4),
            AvatarEntity(R.drawable.img5),
            AvatarEntity(R.drawable.img6),
            AvatarEntity(R.drawable.img7),
            AvatarEntity(R.drawable.img8),
        )

        val adapter = AvatarListAdapter(data)

        adapter.onItemSelected = { avatar ->
            val result = Intent().apply {
                putExtra("EXTRA_AVATAR_KEY", avatar)
            }

            setResult(RESULT_OK, result)
            finish()
        }

        binding.rvAvatarList.adapter = adapter
        binding.rvAvatarList.layoutManager = GridLayoutManager(this, 3)
    }
}