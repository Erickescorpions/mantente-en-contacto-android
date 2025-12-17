package com.erickvazquezs.mantenteencontacto.ui.fragments.account

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentProfilePhotoSelectionBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


enum class PermissionRequired {
    CAMERA,
    GALLERY
}

class ProfilePhotoSelectionFragment : Fragment() {

    private var _binding: FragmentProfilePhotoSelectionBinding? = null
    private val binding get() = _binding!!


    // launcher para obtener la foto tomada con la camara
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap
            binding.ivProfilePhoto.setImageBitmap(imageBitmap)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                Glide.with(this).load(it).into(binding.ivProfilePhoto)
            }
        }
    }

    companion object {
        const val CAMERA_PERMISSION = 1
        const val GALLERY_PERMISSION = 2
    }

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
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Selecciona una imagen de perfil")
                .setMessage("Selecciona una imagen desde la galeria o la camara")
                .setPositiveButton("Camara") { _, _ ->
                    updateOrRequestPermission(PermissionRequired.CAMERA)
                }
                .setNegativeButton("Galeria") { _, _ ->
                    updateOrRequestPermission(PermissionRequired.GALLERY)
                }
                .setNeutralButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)
    }

    private fun updateOrRequestPermission(permissionRequired: PermissionRequired) {
        val permission = if (permissionRequired == PermissionRequired.CAMERA)
            Manifest.permission.CAMERA
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }

        val permissionGranted = ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

        val permissionsToRequest = mutableListOf<String>()

        if (!permissionGranted) {
            permissionsToRequest.add(permission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            val requestCode = if(permissionRequired == PermissionRequired.CAMERA) CAMERA_PERMISSION else GALLERY_PERMISSION
            ActivityCompat.requestPermissions(requireActivity(), permissionsToRequest.toTypedArray(), requestCode)
        } else {

            if (permissionRequired == PermissionRequired.CAMERA) {
                openCamera()
            } else {
                openGallery()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (permissions[0] == Manifest.permission.CAMERA) openCamera()
            else if (permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE) openGallery()
            else if (permissions[0] == Manifest.permission.READ_MEDIA_IMAGES) openGallery()
        } else {
            if (shouldShowRequestPermissionRationale(permissions[0].toString())) {
                val message = if (requestCode == CAMERA_PERMISSION)
                    "Se requiere el permiso para tomar fotos de perfil"
                else
                    "Se requiere el permiso para seleccionar imágenes de la galería"


                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Permiso requerido")
                    .setMessage(message)
                    .setPositiveButton("Entendido") { _, _ ->
                        updateOrRequestPermission(if (requestCode == 1) PermissionRequired.CAMERA else PermissionRequired.GALLERY)
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Permiso requerido")
                    .setMessage("El permiso se ha negado permanentemente")
                    .setNeutralButton("Ir a configuracion") { _, _ ->
                        startActivity(Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts(
                                "package",
                                requireContext().packageName,
                                null
                            )
                        ))
                    }
            }
        }
    }
}