package com.amasesoft.amaseset.data

import androidx.datastore.core.DataStore
import com.amasesoft.amaseset.domain.WorkoutConfig
import com.amasesoft.amaseset.domain.WorkoutSort
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/** BCP-47 [tag] used for the in-app locale override; `null` means "follow the system". */
@Serializable
enum class AppLanguage(val tag: String?) { System(null), Italian("it"), English("en") }

@Serializable
enum class ThemeMode { System, Light, Dark }

/** Background of the run screen. */
@Serializable
enum class RunBackground {
    /** The whole screen takes the phase color; text, ring and controls in the phase "ink". */
    FullColor,

    /** The theme background lightly tinted with the phase; ring and phase label in the phase color. */
    Dark,
}

@Serializable
data class AppSettings(
    val language: AppLanguage = AppLanguage.System,
    val theme: ThemeMode = ThemeMode.System,
    val workoutSort: WorkoutSort = WorkoutSort.Custom,
    val soundEnabled: Boolean = true,
    /** Extra cue when 10 seconds are left in the current activity. */
    val tenSecondWarning: Boolean = false,
    /** Last configuration used for a quick (unsaved) workout, restored next time. */
    val lastQuickConfig: WorkoutConfig = WorkoutConfig.Default,
    /** Run screen background ("Full-color background" switch in the settings). */
    val runBackground: RunBackground = RunBackground.FullColor,
)

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun update(transform: (AppSettings) -> AppSettings)
}

class DataStoreSettingsRepository(private val dataStore: DataStore<AppSettings>) : SettingsRepository {
    override val settings: Flow<AppSettings> = dataStore.data.orDefaultOnIoError { AppSettings() }

    override suspend fun update(transform: (AppSettings) -> AppSettings) {
        dataStore.updateData(transform)
    }
}
