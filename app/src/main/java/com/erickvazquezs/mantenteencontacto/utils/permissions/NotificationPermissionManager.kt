package com.erickvazquezs.mantenteencontacto.utils.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


class NotificationPermissionManager(
    private val fragment: Fragment,
    private val onResult: (Boolean) -> Unit
) {
    private val launcher =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            onResult(granted)
        }

    fun requestIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onResult(true)
            return
        }

        val granted = ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            onResult(true)
        } else {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
