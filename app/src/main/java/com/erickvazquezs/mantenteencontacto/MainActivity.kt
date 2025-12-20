package com.erickvazquezs.mantenteencontacto

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.erickvazquezs.mantenteencontacto.Extensions.dataStore
import com.erickvazquezs.mantenteencontacto.databinding.ActivityMainBinding
import com.erickvazquezs.mantenteencontacto.ui.connectivity.NoInternetFragment
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.erickvazquezs.mantenteencontacto.utils.connectivity.NetworkMonitor
import com.erickvazquezs.mantenteencontacto.utils.notifications.NotificationHelper
import com.erickvazquezs.mantenteencontacto.utils.notifications.TokenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val fullScreenDestinations = setOf(
        R.id.mainOnboardingFragment2,
        R.id.loginFragment,
        R.id.registerFragment,
        R.id.profilePhotoSelectionFragment
    )

    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // creamos el canal de notificaciones
        NotificationHelper.createNotificationChannel(this)

        // obtenemos el token de FCM
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d(Constants.LOGTAG, "FCM token obtenido: $token")

                TokenManager.cacheToken(token)
                TokenManager.trySaveToken()
            }
            .addOnFailureListener { e ->
                Log.e(Constants.LOGTAG, "Error obteniendo FCM token", e)
            }

        // configuramos la navegacion
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.container
        ) as NavHostFragment

        navController = navHostFragment.navController

        val bottomNav = binding.bottomNavigation
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in fullScreenDestinations) {
                bottomNav.visibility = View.GONE
            } else {
                bottomNav.visibility = View.VISIBLE
            }
        }

        val graph = navController.navInflater.inflate(R.navigation.nav_graph)

        lifecycleScope.launch {
            val done = dataStore.data.map { prefs -> prefs[booleanPreferencesKey(Constants.ONBOARDING)] ?: false }.first()
            val user = FirebaseAuth.getInstance().currentUser

            val startDestination: Int = when {
                !done -> R.id.mainOnboardingFragment2
                user == null -> R.id.loginFragment
                else -> R.id.mapsFragment
            }
            graph.setStartDestination(startDestination)
            navController.setGraph(graph, intent.extras)
            bottomNav.setupWithNavController(navController)
        }

        // monitor de internet
        networkMonitor = NetworkMonitor(this)

        networkMonitor.isConnected.observe(this) { isConnected ->
            if (!isConnected) {
                showNoInternetFragment()
            } else {
                hideNoInternetFragment()
            }
        }

        networkMonitor.register()
    }

    private fun showNoInternetFragment() {
        if (supportFragmentManager.findFragmentById(R.id.noInternetContainer) != null) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.noInternetContainer, NoInternetFragment())
            .commitAllowingStateLoss()

        binding.noInternetContainer.visibility = View.VISIBLE
    }

    private fun hideNoInternetFragment() {
        binding.noInternetContainer.visibility = View.GONE
    }
}