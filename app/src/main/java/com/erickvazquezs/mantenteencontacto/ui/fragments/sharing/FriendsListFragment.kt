package com.erickvazquezs.mantenteencontacto.ui.fragments.sharing

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentFriendsListBinding
import com.erickvazquezs.mantenteencontacto.ui.adapters.sharing.FriendAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FriendsListFragment : Fragment() {

    private var _binding: FragmentFriendsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FriendsViewModel
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore

        val factory = FriendsViewModelFactory(db)
        viewModel = ViewModelProvider(this, factory).get(FriendsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = Firebase.auth.uid

        if (currentUserId != null) {
            viewModel.getFriends(currentUserId)

            viewModel.friends.observe(viewLifecycleOwner) { friends ->
                if (friends.isEmpty()) {
                    binding.llNoFriendsFound.visibility = View.VISIBLE
                    binding.rvFriendsList.visibility = View.GONE
                } else {
                    binding.llNoFriendsFound.visibility = View.GONE
                    binding.rvFriendsList.visibility = View.VISIBLE

                    binding.rvFriendsList.apply {
                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        adapter = FriendAdapter(friends)
                    }
                }
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
                binding.progressBar.visibility =
                    if (loading) View.VISIBLE else View.GONE
            }

        } else {
            // TODO: manejar error cuando llegue a suceder que llega aqui sin estar autenticado
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}