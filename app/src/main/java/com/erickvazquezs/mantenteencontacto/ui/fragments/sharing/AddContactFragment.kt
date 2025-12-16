package com.erickvazquezs.mantenteencontacto.ui.fragments.sharing

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentAddContactBinding
import com.erickvazquezs.mantenteencontacto.ui.adapters.sharing.UserSearchAdapter
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddContactFragment : Fragment() {
    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: AddContactViewModel
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore

        val factory = AddContactViewModelFactory(db)
        viewModel = ViewModelProvider(this, factory).get(AddContactViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore

        binding.etUsername.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(300L)

                if (query.length > 2) {
                    viewModel.searchUsers(query)
                } else {
                    viewModel.searchUsers("")
                }
            }
        }

        viewModel.usersFound.observe(viewLifecycleOwner) { users ->
            binding.rvUsersFound.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = UserSearchAdapter(users) { user ->
                    Log.d(Constants.LOGTAG, "Se agrego al usuario ${user.username}")
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: Mostrar/Ocultar el ProgressBar (binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}