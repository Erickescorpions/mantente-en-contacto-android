package com.erickvazquezs.mantenteencontacto.ui.fragments.locations

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.FragmentAddNewPlaceBinding
import com.erickvazquezs.mantenteencontacto.models.PlaceDto
import com.erickvazquezs.mantenteencontacto.ui.fragments.auth.RegisterFragmentDirections
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.erickvazquezs.mantenteencontacto.utils.geofence.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class AddNewPlaceFragment : Fragment() {
    private var _binding: FragmentAddNewPlaceBinding? = null
    val binding get() = _binding!!

    private val args by navArgs<AddNewPlaceFragmentArgs>()
    private var selectedLatLng: LatLng? = null
    private var selectedAddress: String? = null

    private lateinit var geofencingClient: GeofencingClient
    private val geofencePendingIntent: PendingIntent by lazy {
        val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(requireContext(), 0, intent, flags)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        selectedLatLng = args.location
        selectedAddress = args.adress

        selectedLatLng?.let { latLng ->
            binding.tvCoordinates.text = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
        }
        selectedAddress?.let { address ->
            binding.tvSelectedAddress.text = address
        }

        binding.btnSavePlace.setOnClickListener {
            if (validate()) {
                return@setOnClickListener
            }

            val newPlace = PlaceDto(
                name = binding.etPlaceName.text.toString(),
                address = selectedAddress,
                latitude = selectedLatLng!!.latitude,
                longitude = selectedLatLng!!.longitude,
                radius = binding.etRadius.text.toString().toInt(),
                userId = Firebase.auth.currentUser?.uid
            )
            savePlace(newPlace)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validate(): Boolean {
        if (selectedLatLng == null) {
            Toast.makeText(
                requireContext(), "Por favor, selecciona un lugar en el mapa primero.",
                Toast.LENGTH_SHORT
            ).show()
            return true
        }

        var isInvalid = false

        val name = binding.etPlaceName.text.toString().trim()
        val radiusText = binding.etRadius.text.toString().trim()

        binding.etPlaceName.error = null
        binding.etRadius.error = null

        if (name.isEmpty()) {
            binding.etPlaceName.error = "El nombre no puede estar vacío"
            isInvalid = true
        }

        val radius = radiusText.toIntOrNull()
        if (radiusText.isEmpty() || radius == null || radius <= 0) {
            binding.etRadius.error = "El radio debe ser un número positivo."
            isInvalid = true
        }

        return isInvalid
    }

    private fun savePlace(place: PlaceDto) {
        val db = Firebase.firestore
        db.collection("places")
            .add(place)
            .addOnSuccessListener { documentReference ->
                place.id = documentReference.id
                addGeofence(place)
            }.addOnFailureListener { exception ->
                Log.e(
                    Constants.LOGTAG,
                    "Error al guardar el lugar en Firestore: ${exception.message}"
                )
                Toast.makeText(
                    requireContext(),
                    "Error al guardar: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(place: PlaceDto) {
        if (place.id == null) {
            Log.e(Constants.LOGTAG, "Error al registrar la Geocerca")
            return
        }

        val geofence = Geofence.Builder()
            .setRequestId(place.id!!)
            .setCircularRegion(
                place.latitude,
                place.longitude,
                place.radius.toFloat()
            )
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setLoiteringDelay(5000)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d(Constants.LOGTAG, "Geocerca ${place.name} ID:${place.id} añadida con éxito.")
                Toast.makeText(requireContext(), "Geocerca Activada: ${place.name}", Toast.LENGTH_SHORT).show()

                findNavController().popBackStack()
            }
            addOnFailureListener { e ->
                Log.e(Constants.LOGTAG, "Error al agregar Geocerca localmente: ${e.message}", e)
                Toast.makeText(requireContext(), "Error al activar la Geocerca local.", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        }
    }

}