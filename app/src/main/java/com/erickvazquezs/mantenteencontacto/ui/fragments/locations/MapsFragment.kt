package com.erickvazquezs.mantenteencontacto.ui.fragments.locations

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

import androidx.fragment.app.viewModels
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentMapsBinding
import com.erickvazquezs.mantenteencontacto.utils.permissions.BackgroundLocationPermissionExplanationProvider
import com.erickvazquezs.mantenteencontacto.utils.permissions.FineLocationPermissionExplanationProvider
import com.erickvazquezs.mantenteencontacto.utils.permissions.PermissionExplanationProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private val callback = OnMapReadyCallback { map -> googleMap = map }

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startGoogleMap()
            } else {
                val permanentlyDenied = !shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (permanentlyDenied) {
                    openAppSettings()
                } else {
                    showPermissionExplanationDialog()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        checkAndRequestPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkAndRequestPermission() {
        val fineGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted) {
            startGoogleMap()
        } else {
            showPermissionExplanationDialog()
        }
    }

    private fun showPermissionExplanationDialog() {
        val provider = FineLocationPermissionExplanationProvider()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(provider.getPermissionText())
            .setMessage(provider.getExplanation(true))
            .setPositiveButton("Entendido") { dialog, _ ->
                dialog.dismiss()
                permissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                showPermissionDeniedUI()
            }
            .show()
    }

    private fun startGoogleMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(binding.mapContainer.id) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun showPermissionDeniedUI() {
        binding.mapContainer.visibility = View.GONE
        binding.editSearchLocation.visibility = View.GONE

        binding.permissionDenyUi.visibility = View.VISIBLE
        binding.errorMessageTextView.visibility = View.VISIBLE
        // si el permiso ya esta negado permanentemente

        binding.errorMessageTextView.text =
            getString(R.string.map_permission_denied)

        binding.retryButton.setOnClickListener {
            binding.permissionDenyUi.visibility = View.GONE
            binding.mapContainer.visibility = View.VISIBLE
            binding.editSearchLocation.visibility = View.VISIBLE
            checkAndRequestPermission()
        }
    }

    private fun openAppSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", requireContext().packageName, null)
            )
        )
    }
}
