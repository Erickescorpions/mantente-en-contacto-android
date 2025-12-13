package com.erickvazquezs.mantenteencontacto.ui.fragments.locations

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.customview.R
import androidx.fragment.app.viewModels
import com.erickvazquezs.mantenteencontacto.databinding.FragmentMapsBinding
import com.erickvazquezs.mantenteencontacto.utils.permissions.BackgroundLocationPermissionExplanationProvider
import com.erickvazquezs.mantenteencontacto.utils.permissions.FineLocationPermissionExplanationProvider
import com.erickvazquezs.mantenteencontacto.utils.permissions.PermissionExplanationProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private val callback = OnMapReadyCallback { map ->
        googleMap = map
    }

    private val permissionsViewModel: MapsPermissionsViewModel by viewModels()
    private var fineLocationPermissionGranted = false
    private var backgroundLocationPermissionGranted = false
    private var coarseLocationPermissionGranted = false
    private var permissionsToRequest = mutableListOf<String>()
    private val permissionsLauncher =  registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult: Map<String, Boolean> ->
        val allGranted = permissionsResult.all {map ->
            map.value
        }

        if (allGranted) {
            actionPermissionsGranted()
        } else {
            permissionsToRequest.forEach { permission ->
                permissionsViewModel.onPermissionResult(
                    permission,
                    permissionsResult[permission] == true
                )
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionsViewModel.permissionToRequest.observe(viewLifecycleOwner) { queue ->
            queue.reversed().forEach { permission ->
                showPermissionExplanationDialog(
                    when(permission) {
                        Manifest.permission.ACCESS_FINE_LOCATION -> {
                            FineLocationPermissionExplanationProvider()
                        }

                        Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                            BackgroundLocationPermissionExplanationProvider()
                        }
                        else -> return@forEach
                    },
                    shouldShowRequestPermissionRationale(permission),
                    {
                        permissionsViewModel.dismissDialogRemovePermission()
                    }, {
                        permissionsViewModel.dismissDialogRemovePermission()
                        updateOrRequestPermissions()
                    }, {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts(
                                    "package",
                                    requireContext().packageName,
                                    null
                                )
                            )
                        )
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateOrRequestPermissions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateOrRequestPermissions() {
        fineLocationPermissionGranted = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        backgroundLocationPermissionGranted =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

        coarseLocationPermissionGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationPermissionGranted)
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if(!backgroundLocationPermissionGranted)
            permissionsToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        if(!coarseLocationPermissionGranted)
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        if(permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(
                permissionsToRequest.toTypedArray()
            )
        } else {
            actionPermissionsGranted()
        }
    }

    private fun actionPermissionsGranted() {
        startGoogleMap()
    }

    private fun startGoogleMap() {
        val mapFragment = childFragmentManager.findFragmentById(binding.mapContainer.id) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun showPermissionExplanationDialog(
        permissionExplanationProvider: PermissionExplanationProvider,
        isNotPermanentlyDeclined: Boolean,
        onDismiss: () -> Unit,
        onOkClick: () -> Unit,
        onGoToAppSettingsClick: () -> Unit
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(permissionExplanationProvider.getPermissionText())
            .setMessage(permissionExplanationProvider.getExplanation(isNotPermanentlyDeclined))
            .setPositiveButton(if (isNotPermanentlyDeclined) "Entendido" else "Configuración") { dialog, _ ->
                dialog.dismiss()
                if (isNotPermanentlyDeclined) onOkClick()
                else onGoToAppSettingsClick()
            }
            .setOnDismissListener { dialog ->
                dialog.dismiss()
                onDismiss()
            }
            .show()
    }
}