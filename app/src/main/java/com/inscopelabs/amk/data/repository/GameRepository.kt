package com.inscopelabs.amk.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.goDataStore: DataStore<Preferences> by preferencesDataStore(name = "amk_go_snapshots")

class GameRepository(private val context: Context) {
  private val standardSnapshotKey = stringPreferencesKey("standard_go_snapshot")

  val savedStandardSnapshotFlow: Flow<String?> =
    context.goDataStore.data.map { prefs -> prefs[standardSnapshotKey] }

  suspend fun saveStandardSnapshot(json: String) {
    context.goDataStore.edit { prefs ->
      prefs[standardSnapshotKey] = json
    }
  }

  suspend fun loadStandardSnapshot(): String? =
    context.goDataStore.data.first()[standardSnapshotKey]
}
