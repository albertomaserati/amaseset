package com.amasesoft.amaseset.ui.home

import com.amasesoft.amaseset.data.AppSettings
import com.amasesoft.amaseset.data.FakeSettingsRepository
import com.amasesoft.amaseset.data.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import com.amasesoft.amaseset.data.FakeWorkoutRepository
import com.amasesoft.amaseset.data.FailingWorkoutRepository
import com.amasesoft.amaseset.data.WorkoutStore
import com.amasesoft.amaseset.data.create
import com.amasesoft.amaseset.domain.CyclePlan
import com.amasesoft.amaseset.domain.WorkoutConfig
import com.amasesoft.amaseset.domain.WorkoutSort
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    // Shared by Main (viewModelScope) and runTest, so collectors start eagerly.
    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val store = listOf("beta" to 3, "Alpha" to 1, "gamma" to 2).fold(WorkoutStore()) { s, (name, cycles) ->
        s.create(name, "", WorkoutConfig(prepareSeconds = 0, plan = CyclePlan.Uniform(10, 0, cycles)), 0).first
    }

    @Test
    fun sortIsPersistedAndApplied() = runTest(dispatcher) {
        val settings = FakeSettingsRepository()
        val vm = HomeViewModel(FakeWorkoutRepository(store), settings)
        backgroundScope.launch { vm.state.collect {} }

        assertEquals(listOf("beta", "Alpha", "gamma"), vm.state.value.workouts.map { it.name })
        vm.setSort(WorkoutSort.Name)
        assertEquals(WorkoutSort.Name, settings.state.value.workoutSort)
        assertEquals(listOf("Alpha", "beta", "gamma"), vm.state.value.workouts.map { it.name })
        vm.setSort(WorkoutSort.Longest)
        assertEquals(listOf("beta", "gamma", "Alpha"), vm.state.value.workouts.map { it.name })
    }

    @Test
    fun reorderingForcesCustomSort() = runTest(dispatcher) {
        val settings = FakeSettingsRepository()
        val vm = HomeViewModel(FakeWorkoutRepository(store), settings)
        backgroundScope.launch { vm.state.collect {} }
        vm.setSort(WorkoutSort.Name)

        vm.setReordering(true)
        assertEquals(WorkoutSort.Custom, vm.state.value.sort)
        assertTrue(vm.state.value.isReordering)
        vm.move(2, -1)
        assertEquals(listOf("Alpha", "beta", "gamma"), vm.state.value.workouts.map { it.name })
        // Choosing another sort leaves reorder mode.
        vm.setSort(WorkoutSort.Newest)
        assertFalse(vm.state.value.isReordering)
    }

    @Test
    fun moveToEdgeSwitchesToCustomOrder() = runTest(dispatcher) {
        val settings = FakeSettingsRepository()
        val vm = HomeViewModel(FakeWorkoutRepository(store), settings)
        backgroundScope.launch { vm.state.collect {} }
        vm.setSort(WorkoutSort.Name)
        vm.moveToEdge(3, toTop = true)
        assertEquals(WorkoutSort.Custom, vm.state.value.sort)
        assertEquals(listOf("gamma", "beta", "Alpha"), vm.state.value.workouts.map { it.name })
    }

    @Test
    fun writeFailureIsReported() = runTest(dispatcher) {
        val vm = HomeViewModel(FailingWorkoutRepository(), FakeSettingsRepository())
        val events = mutableListOf<HomeEvent>()
        backgroundScope.launch { vm.events.collect { events += it } }
        vm.delete(1)
        assertEquals(listOf<HomeEvent>(HomeEvent.StorageError), events)
    }

    @Test
    fun readErrorIsAnErrorStateNotAnEmptyList() = runTest(dispatcher) {
        val vm = HomeViewModel(FailingWorkoutRepository(), FakeSettingsRepository())
        backgroundScope.launch { vm.state.collect {} }
        val state = vm.state.value
        assertTrue(state.loadError)
        assertFalse(state.isLoading)
        assertTrue(state.workouts.isEmpty())
        // Creating (FAB), sorting and reordering need a known list.
        assertFalse(state.isListAvailable)
        vm.setReordering(true)
        assertFalse(vm.state.value.isReordering)
    }

    @Test
    fun retryRereadsAndShowsTheList() = runTest(dispatcher) {
        val repo = FakeWorkoutRepository(store).apply { failReads = true }
        val vm = HomeViewModel(repo, FakeSettingsRepository())
        backgroundScope.launch { vm.state.collect {} }
        assertTrue(vm.state.value.loadError)

        vm.reload() // still failing
        assertTrue(vm.state.value.loadError)

        repo.failReads = false
        vm.reload()
        assertFalse(vm.state.value.loadError)
        assertTrue(vm.state.value.isListAvailable)
        assertEquals(listOf("beta", "Alpha", "gamma"), vm.state.value.workouts.map { it.name })
    }

    @Test
    fun startsInLoadingStateWithoutEmptyList() = runTest(dispatcher) {
        val vm = HomeViewModel(FakeWorkoutRepository(store), FakeSettingsRepository())
        // Before the first read completes nothing claims "no workouts" and nothing can be created.
        assertTrue(vm.state.value.isLoading)
        assertFalse(vm.state.value.isListAvailable)
    }

    @Test
    fun quickStartShowsTheLastQuickConfig() = runTest(dispatcher) {
        val last = WorkoutConfig(prepareSeconds = 5, sets = 2, plan = CyclePlan.Uniform(40, 20, 6))
        val settings = FakeSettingsRepository(AppSettings(lastQuickConfig = last))
        val vm = HomeViewModel(FakeWorkoutRepository(store), settings)
        backgroundScope.launch { vm.state.collect {} }

        assertEquals(last, vm.state.value.quickConfig)
        // 5 + 2 × (6 × 40 + 5 × 20) + 60 between sets
        assertEquals(745, vm.state.value.quickTotalSeconds)

        val next = WorkoutConfig(plan = CyclePlan.Uniform(30, 0, 3))
        settings.update { it.copy(lastQuickConfig = next) }
        assertEquals(next, vm.state.value.quickConfig)
    }

    @Test
    fun quickStartIsAvailableEvenWhenTheWorkoutsCannotBeRead() = runTest(dispatcher) {
        val vm = HomeViewModel(FailingWorkoutRepository(), FakeSettingsRepository())
        backgroundScope.launch { vm.state.collect {} }
        assertTrue(vm.state.value.loadError)
        assertEquals(WorkoutConfig.Default, vm.state.value.quickConfig)
    }

    @Test
    fun quickStartIsUnknownUntilTheSettingsAreRead() = runTest(dispatcher) {
        val last = WorkoutConfig(sets = 3, plan = CyclePlan.Uniform(45, 15, 5))
        val settings = MutableSharedFlow<AppSettings>()
        val repo = object : SettingsRepository {
            override val settings: Flow<AppSettings> = settings
            override suspend fun update(transform: (AppSettings) -> AppSettings) = Unit
        }
        val vm = HomeViewModel(FakeWorkoutRepository(store), repo)
        backgroundScope.launch { vm.state.collect {} }

        // Not the default: nothing to show or start yet.
        assertNull(vm.state.value.quickConfig)
        assertNull(vm.state.value.quickTotalSeconds)

        settings.emit(AppSettings(lastQuickConfig = last))
        assertEquals(last, vm.state.value.quickConfig)
    }
}
