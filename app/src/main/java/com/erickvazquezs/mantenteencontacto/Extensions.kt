package com.erickvazquezs.mantenteencontacto

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.erickvazquezs.mantenteencontacto.utils.Constants

object Extensions {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        Constants.DATASTORE_FILE
    )
}