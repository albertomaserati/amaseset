package com.amasesoft.amaseset.di

import com.amasesoft.amaseset.data.AppSettings
import com.amasesoft.amaseset.data.DataStoreGymRepository
import com.amasesoft.amaseset.data.DataStoreSettingsRepository
import com.amasesoft.amaseset.data.DataStoreWorkoutRepository
import com.amasesoft.amaseset.data.GymRepository
import com.amasesoft.amaseset.data.GymStore
import com.amasesoft.amaseset.data.SettingsRepository
import com.amasesoft.amaseset.data.WorkoutRepository
import com.amasesoft.amaseset.data.WorkoutStore
import com.amasesoft.amaseset.data.createJsonDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Manual dependency injection: the app graph is tiny, so a single container is enough.
 *
 * Must be a process-wide singleton (see `appContainer(context)` on Android and
 * `MainViewController` on iOS) because each DataStore file allows one active instance.
 *
 * @param storageDirectory absolute path of a private, writable directory.
 * @param appVersion version of the installed app, read from the platform (shown in Settings > About).
 */
class AppContainer(storageDirectory: String, val appVersion: AppVersion) {

    val workoutRepository: WorkoutRepository = DataStoreWorkoutRepository(
        createJsonDataStore(storageDirectory, "workouts.json", WorkoutStore.serializer(), WorkoutStore()),
    )

    val settingsRepository: SettingsRepository = DataStoreSettingsRepository(
        createJsonDataStore(storageDirectory, "settings.json", AppSettings.serializer(), AppSettings()),
    )

    /** Gym routines ("schede"): a separate file, independent from the timer data. */
    val gymRepository: GymRepository = DataStoreGymRepository(
        createJsonDataStore(storageDirectory, "gym.json", GymStore.serializer(), GymStore()),
    )

    /** Application-lifetime scope (the container lives as long as the process). */
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Settings shared by the whole UI. Being hot and process-scoped, after the first load it
     * already holds a value when the Activity is re-created (no empty frame on rotation).
     * `null` only until the very first read completes.
     */
    val settings: StateFlow<AppSettings?> =
        settingsRepository.settings.stateIn(appScope, SharingStarted.Eagerly, null)
}

/** User-visible version (`versionName` / `CFBundleShortVersionString`) and build number. */
data class AppVersion(val name: String, val build: String)
