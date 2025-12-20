package com.erickvazquezs.mantenteencontacto.ui.fragments.sharing

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentFriendsListBinding
import com.erickvazquezs.mantenteencontacto.ui.adapters.sharing.FriendAdapter
import com.erickvazquezs.mantenteencontacto.utils.permissions.NotificationPermissionManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FriendsListFragment : Fragment() {

    private var _binding: FragmentFriendsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FriendsViewModel
    private lateinit var db: FirebaseFirestore
    private lateinit var permissionManager: NotificationPermissionManager

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

        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            findNavController().navigate(
                FriendsListFragmentDirections.actionFriendsListFragmentToLoginFragment()
            )
            return
        }

        permissionManager = NotificationPermissionManager(this) { granted ->
            if (!granted) {
                Toast.makeText(
                    requireContext(),
                    "Activa las notificaciones para recibir avisos de tus amigos",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        permissionManager.requestIfNeeded()

        viewModel.getFriends(currentUser.uid)

        viewModel.friends.observe(viewLifecycleOwner) { friends ->
            if (friends.isEmpty()) {
                binding.llNoFriendsFound.visibility = View.VISIBLE
                binding.rvFriendsList.visibility = View.GONE
            } else {
                binding.llNoFriendsFound.visibility = View.GONE
                binding.rvFriendsList.visibility = View.VISIBLE

                binding.rvFriendsList.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = FriendAdapter(friends)
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility =
                if (loading) View.VISIBLE else View.GONE
        }

        binding.flBtnAddFriends.setOnClickListener { goToAddFriend() }
        binding.btnAddFriends.setOnClickListener { goToAddFriend() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goToAddFriend() {
        findNavController().navigate(
            FriendsListFragmentDirections.actionFriendsListFragmentToAddContactFragment()
        )
    }
}