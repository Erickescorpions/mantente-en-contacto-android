package com.erickvazquezs.mantenteencontacto.ui.fragments.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentProfilePhotoSelectionBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfilePhotoSelectionFragment : Fragment() {

    private var _binding: FragmentProfilePhotoSelectionBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilePhotoSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnChoosePhoto.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(512, 512)
                .start()
        }

        binding.btnContinue.setOnClickListener {
            uploadImageAndContinue()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            fileUri?.let { uri ->
                imageUri = uri
                Glide.with(this).load(uri).into(binding.ivProfilePhoto)
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageAndContinue() {
        val user = Firebase.auth.currentUser ?: return
        val uri = imageUri ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val storageRef = Firebase.storage.reference
                val avatarRef = storageRef.child("images/${user.uid}")

                val mimeType = requireContext()
                    .contentResolver
                    .getType(uri) ?: "image/jpeg"

                val metadata = StorageMetadata.Builder()
                    .setContentType(mimeType)
                    .build()

                avatarRef.putFile(uri, metadata).await()
                val downloadUrl = avatarRef.downloadUrl.await()

                Firebase.firestore
                    .collection("users")
                    .document(user.uid)
                    .update("avatarUrl", downloadUrl.toString())
                    .await()

                continueToApp()

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error guardando imagen, intenta de nuevo",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun continueToApp() {
        findNavController().navigate(
            ProfilePhotoSelectionFragmentDirections.actionProfilePhotoSelectionFragmentToMapsFragment()
        )
    }
}