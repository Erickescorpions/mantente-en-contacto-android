package com.erickvazquezs.mantenteencontacto.ui.fragments.locations

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentMapsBinding
import com.erickvazquezs.mantenteencontacto.models.PlaceDto
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.erickvazquezs.mantenteencontacto.utils.geofence.GeofenceManager
import com.erickvazquezs.mantenteencontacto.utils.permissions.FineLocationPermissionExplanationProvider
import com.erickvazquezs.mantenteencontacto.utils.permissions.LocationPermissionManager
import com.erickvazquezs.mantenteencontacto.utils.permissions.LocationPermissionResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
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
        viewModel.startSubscription()
        getDeviceLocation()
    }
    var selectedLocation: LatLng? = null
    private var selectedPlaceMarker: Marker? = null
    private val viewModel: MapsViewModel by viewModels()
    private var mapInitialized = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val DEFAULT_ZOOM = 15f
    private var geofencesRestored = false


    // permisos
    private val backgroundLocationPermission = Manifest.permission.ACCESS_BACKGROUND_LOCATION
    private val backgroundPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                showBackgroundPermissionDeniedWarning()
            }
        }

    private lateinit var permissionManager: LocationPermissionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        permissionManager.check()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        permissionManager = LocationPermissionManager(this) { result ->
            when (result) {
                LocationPermissionResult.GRANTED -> {
                    showMapUI()
                    startGoogleMap()
                }

                LocationPermissionResult.EXPLANATION -> {
                    showPermissionDeniedUI()
                }

                LocationPermissionResult.DENIED_UI -> {
                    showPermissionDeniedUI()
                }

                LocationPermissionResult.PERMANENTLY_DENIED -> {
                    openAppSettings()
                }
            }
        }

        binding.btnAddPlace.setOnClickListener {
            val location = selectedLocation
            if (location == null) {
                Toast.makeText(requireContext(), "Selecciona un lugar en el mapa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnAddPlace.visibility = View.GONE

            findNavController().navigate(
                MapsFragmentDirections
                    .actionMapsFragmentToAddNewPlaceFragment(location)
            )
        }

        binding.btnUserLocation.setOnClickListener {
            if (isMapReady()) {
                getDeviceLocation()
            }
        }

        binding.tvPermissionMsg.text = getString(R.string.map_permission_description)
        binding.btnRetry.setOnClickListener {
            showPermissionExplanationDialog()
        }

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestBackgroundLocationPermission() {
        val backgroundGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            backgroundLocationPermission
        ) == PackageManager.PERMISSION_GRANTED

        if (backgroundGranted) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Permiso de Ubicación en Segundo Plano Necesario")
                .setMessage("Para que la aplicación te avise cuando entres o salgas de un lugar registrado, necesitamos acceder a la ubicación en segundo plano. Esto garantiza que las notificaciones funcionen incluso cuando la aplicación está cerrada.")
                .setPositiveButton("Continuar") { dialog, _ ->
                    dialog.dismiss()
                    backgroundPermissionLauncher.launch(backgroundLocationPermission)
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                    showBackgroundPermissionDeniedWarning()
                }
                .show()
        }
    }

    private fun showBackgroundPermissionDeniedWarning() {
        Toast.makeText(
            requireContext(),
            "Advertencia: El monitoreo de geocercas será limitado sin el permiso de fondo.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showMapUI() {
        binding.permissionDenyUi.visibility = View.GONE
        binding.mapContainer.visibility = View.VISIBLE
        binding.btnUserLocation.visibility = View.VISIBLE
    }

    private fun showPermissionDeniedUI() {
        binding.mapContainer.visibility = View.GONE
        binding.permissionDenyUi.visibility = View.VISIBLE
        binding.btnUserLocation.visibility = View.GONE
    }

    // Dialogos para los permisos de ubicacion
    private fun showPermissionExplanationDialog() {
        val provider = FineLocationPermissionExplanationProvider()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(provider.getPermissionText())
            .setMessage(provider.getExplanation(true))
            .setPositiveButton("Entendido") { dialog, _ ->
                dialog.dismiss()
                permissionManager.request()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                showPermissionDeniedUI()
            }
            .show()
    }

    private fun startGoogleMap() {
        if (mapInitialized) return

        val mapFragment =
            childFragmentManager.findFragmentById(binding.mapContainer.id)
                    as? SupportMapFragment

        mapFragment?.getMapAsync(callback)
        mapInitialized = true
    }


    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        if (!isMapReady()) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                handleNewLocation(location)
            }

            requestBackgroundLocationPermission()
        }.addOnFailureListener { e ->
            // Error al obtener la ubicación
            Log.e(Constants.LOGTAG, "Error al obtener la última ubicación", e)
            requestBackgroundLocationPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleNewLocation(location: Location) {
        if (!isMapReady()) return

        val currentLatLng = LatLng(location.latitude, location.longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
        googleMap.isMyLocationEnabled = true
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

    private fun observeViewModel() {
        viewModel.registeredPlaces.observe(viewLifecycleOwner) { placesList ->
            if (isMapReady()) {
                googleMap.clear()

                placesList.forEach { place ->
                    drawPlaceMarker(place)
                }

                selectedPlaceMarker?.let { marker ->
                    selectedPlaceMarker = googleMap.addMarker(
                        MarkerOptions()
                            .position(marker.position)
                            .title(marker.title)
                    )
                }
            }

            restoreGeofencesIfNeeded(placesList)
        }
    }

    private fun restoreGeofencesIfNeeded(places: List<PlaceDto>) {
        if (geofencesRestored) return
        if (places.isEmpty()) return

        places.forEach { place ->
            GeofenceManager.add(requireContext(), place) { success ->
                if (success) {
                    Log.d(
                        Constants.LOGTAG,
                        "Geocerca restaurada: ${place.name}"
                    )
                } else {
                    Log.e(
                        Constants.LOGTAG,
                        "Error restaurando geocerca: ${place.name}"
                    )
                }
            }
        }

        geofencesRestored = true
    }

    private fun drawPlaceMarker(place: PlaceDto) {
        val latLng = LatLng(place.latitude, place.longitude)

        googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(place.name)
                .snippet(place.address)
        )

        googleMap.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(place.radius.toDouble())
                .strokeColor(resources.getColor(R.color.yellow, null))
                .fillColor(resources.getColor(R.color.light_yellow, null) and 0x33FFFFFF)
        )
    }

    private fun isMapReady(): Boolean {
        return ::googleMap.isInitialized
    }
}
