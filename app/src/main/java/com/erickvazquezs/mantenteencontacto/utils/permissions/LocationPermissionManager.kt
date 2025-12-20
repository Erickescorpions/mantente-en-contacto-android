package com.erickvazquezs.mantenteencontacto.utils.permissions

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class LocationPermissionManager(
    private val fragment: Fragment,
    private val onResult: (LocationPermissionResult) -> Unit
) {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val launcher =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->

            val granted = result.any { it.value }

            if (granted) {
                onResult(LocationPermissionResult.GRANTED)
                return@registerForActivityResult
            }

            val permanentlyDenied = permissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    fragment.requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED &&
                        !fragment.shouldShowRequestPermissionRationale(permission)
            }

            if (permanentlyDenied) {
                onResult(LocationPermissionResult.PERMANENTLY_DENIED)
            } else {
                onResult(LocationPermissionResult.DENIED_UI)
            }
        }

    fun check() {
        val granted = permissions.any {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (granted) {
            onResult(LocationPermissionResult.GRANTED)
        } else {

            val permanentlyDenied = permissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    fragment.requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED &&
                        !fragment.shouldShowRequestPermissionRationale(permission)
            }

            if (permanentlyDenied) {
                onResult(LocationPermissionResult.DENIED_UI)
            } else {
                onResult(LocationPermissionResult.EXPLANATION)
            }
        }
    }

    fun request() {
        launcher.launch(permissions)
    }
}
