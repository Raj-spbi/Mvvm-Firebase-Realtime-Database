package com.ex.mvvmbasics.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class UserDetails(private val context: Context) {


    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("pref000")
        val CURRENT_USER_ID = stringPreferencesKey("CURRENT_USER_ID")
    }

    suspend fun storeUser(fbId: String) {
        context.dataStore.edit {
            it[CURRENT_USER_ID] = fbId
        }
    }

    suspend fun clearRecords() {
        context.dataStore.edit {
            it.clear()
        }
    }


    fun getUserName() = context.dataStore.data.map {
        it[CURRENT_USER_ID] ?: ""
    }

}