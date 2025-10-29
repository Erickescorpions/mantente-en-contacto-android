package com.erickvazquezs.mantenteencontacto.ui.fragments.account

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentChooseAvatarBinding
import com.erickvazquezs.mantenteencontacto.models.AvatarEntity
import com.erickvazquezs.mantenteencontacto.ui.adapters.AvatarListAdapter

class ChooseAvatarFragment : Fragment() {

    private var _binding: FragmentChooseAvatarBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            findNavController().navigate(
                ChooseAvatarFragmentDirections.actionChooseAvatarFragmentToRegisterFragment(
                    avatar
                )
            )
        }
        binding.rvAvatarList.adapter = adapter
        binding.rvAvatarList.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseAvatarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}