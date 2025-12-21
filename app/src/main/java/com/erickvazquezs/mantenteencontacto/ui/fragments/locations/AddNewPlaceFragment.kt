package com.erickvazquezs.mantenteencontacto.ui.fragments.locations

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
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.erickvazquezs.mantenteencontacto.utils.geofence.GeofenceManager
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                GeofenceManager.add(requireContext(), place) {success ->
                    if (success) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.place_saved, place.name),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.error_geofence),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    findNavController().popBackStack()
                }
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
}