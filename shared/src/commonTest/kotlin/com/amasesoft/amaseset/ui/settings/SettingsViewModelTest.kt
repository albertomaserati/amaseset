package com.amasesoft.amaseset.ui.settings

import com.amasesoft.amaseset.data.FakeSettingsRepository
import com.amasesoft.amaseset.data.RunBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun fullColorSwitchWritesTheRunBackground() = runTest(dispatcher) {
        val repository = FakeSettingsRepository()
        val vm = SettingsViewModel(repository)
        backgroundScope.launch { vm.settings.collect {} }
        assertEquals(RunBackground.FullColor, vm.settings.value?.runBackground)

        vm.setFullColorRunBackground(false)
        assertEquals(RunBackground.Dark, repository.state.value.runBackground)
        assertEquals(RunBackground.Dark, vm.settings.value?.runBackground)

        vm.setFullColorRunBackground(true)
        assertEquals(RunBackground.FullColor, repository.state.value.runBackground)
    }
}
