package com.erickvazquezs.mantenteencontacto.ui.fragments.locations

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController

import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentMapsBinding
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.erickvazquezs.mantenteencontacto.utils.permissions.FineLocationPermissionExplanationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MapsFragment : Fragment(), GoogleMap.OnMapClickListener {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        googleMap.setOnMapClickListener(this)
    }
    var selectedLocation: LatLng? = null
    private var selectedPlaceMarker: Marker? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val DEFAULT_ZOOM = 15f

    private val locationPermissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsResult ->
            val fineGranted = permissionsResult[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted =
                permissionsResult[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            val locationAccessGranted = fineGranted || coarseGranted

            if (locationAccessGranted) {
                startGoogleMap()
            } else {
                val permanentlyDenied = !shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || !shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (permanentlyDenied) {
                    openAppSettings()
                } else {
                    showPermissionDeniedUI()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.btnAddPlace.setOnClickListener {
            // ocultamos el boton
            binding.btnAddPlace.visibility = View.GONE
            // nos movemos a la nueva vista
            findNavController().navigate(
                MapsFragmentDirections.actionMapsFragmentToAddNewPlaceFragment(
                    location = selectedLocation!!
                )
            )
        }
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
        val coarseGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationAccessGranted = fineGranted || coarseGranted

        if (locationAccessGranted) {
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
                permissionsLauncher.launch(locationPermissions.toTypedArray())
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
        getDeviceLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                handleNewLocation(location)
            }
        }.addOnFailureListener { e ->
            // Error al obtener la ubicación
            Log.e(Constants.LOGTAG, "Error al obtener la última ubicación", e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleNewLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
        googleMap.isMyLocationEnabled = true

        Log.d(
            Constants.LOGTAG,
            "Ubicación obtenida: ${currentLatLng.latitude}, ${currentLatLng.longitude}"
        )
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

    // manejando el tap
    override fun onMapClick(latLng: LatLng) {
        handleMapTap(latLng)
    }

    private fun handleMapTap(latLng: LatLng) {
        selectedPlaceMarker?.remove()

        val newMarker = googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Lugar Seleccionado")
        )

        selectedPlaceMarker = newMarker

        selectedLocation = latLng
        // mostramos el boton
        binding.btnAddPlace.visibility = View.VISIBLE
    }
}
