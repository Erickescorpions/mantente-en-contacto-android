package com.erickvazquezs.mantenteencontacto.ui.fragments.account

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
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
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentProfilePhotoSelectionBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfilePhotoSelectionFragment : Fragment() {

    private var _binding: FragmentProfilePhotoSelectionBinding? = null
    private val binding get() = _binding!!

    private var cameraPermissionGranted = false
    private  var isCameraActive = false
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val imageBitmap = intent?.extras?.get("data") as Bitmap
            binding.ivProfilePhoto.setImageBitmap(imageBitmap)
        }
    }

    companion object {
        const val CAMERA_PERMISSION = 1
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
            updateOrRequestPermission()
            resultLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun actionPermissionGranted() {
        Toast.makeText(requireContext(), "El permiso a la camara se ha concedido", Toast.LENGTH_SHORT).show()
    }

    private fun updateOrRequestPermission() {
        cameraPermissionGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        val permissionsToRequest = mutableListOf<String>()

        if (!cameraPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (permissionsToRequest.isNotEmpty()) {
            // pedimos permisos
            ActivityCompat.requestPermissions(requireActivity(), permissionsToRequest.toTypedArray(),
                CAMERA_PERMISSION)
        } else {
            // tenemos el permiso de la camara
            actionPermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            actionPermissionGranted()
        } else {
            if (shouldShowRequestPermissionRationale(permissions[0].toString())) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Permiso requerido")
                    .setMessage("Se requiere el permiso solamente para tomar fotos de perfil")
                    .setPositiveButton("Entendido") { _, _ ->
                        updateOrRequestPermission()
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