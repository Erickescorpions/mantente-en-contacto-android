package com.erickvazquezs.mantenteencontacto

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.erickvazquezs.mantenteencontacto.Extensions.dataStore
import com.erickvazquezs.mantenteencontacto.databinding.ActivityMainBinding
import com.erickvazquezs.mantenteencontacto.ui.fragments.auth.LoginFragmentDirections
import com.erickvazquezs.mantenteencontacto.ui.fragments.onboarding.MainOnboardingFragment
import com.erickvazquezs.mantenteencontacto.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val fullScreenDestinations = setOf(
        R.id.mainOnboardingFragment2,
        R.id.loginFragment,
        R.id.registerFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

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
    }
}