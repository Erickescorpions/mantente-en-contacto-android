package com.erickvazquezs.mantenteencontacto.utils.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NetworkMonitor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    private fun checkConnection() {
        val network = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(network)

        _isConnected.postValue(
            caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        )
    }

    private val callback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            checkConnection()
        }

        override fun onLost(network: Network) {
            checkConnection()
        }
    }

    fun register() {
        checkConnection()
        connectivityManager.registerDefaultNetworkCallback(callback)
    }
}
