package com.amasesoft.amaseset.data

import kotlin.test.Test
import kotlin.test.assertEquals

class AppSettingsTest {

    @Test
    fun settingsSavedBeforeTheRunBackgroundOptionReadAsFullColor() {
        // settings.json written by a version without the field.
        val old = """{"language":"Italian","theme":"Dark","workoutSort":"Name","soundEnabled":false}"""
        val settings = StorageJson.decodeFromString(AppSettings.serializer(), old)
        assertEquals(RunBackground.FullColor, settings.runBackground)
        assertEquals(ThemeMode.Dark, settings.theme)
        assertEquals(false, settings.soundEnabled)
    }

    @Test
    fun runBackgroundIsPersisted() {
        val json = StorageJson.encodeToString(AppSettings.serializer(), AppSettings(runBackground = RunBackground.Dark))
        assertEquals(RunBackground.Dark, StorageJson.decodeFromString(AppSettings.serializer(), json).runBackground)
    }
}
