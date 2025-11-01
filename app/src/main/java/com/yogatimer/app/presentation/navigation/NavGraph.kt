package com.yogatimer.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yogatimer.app.presentation.create.CreateWorkoutScreen
import com.yogatimer.app.presentation.home.HomeScreen
import com.yogatimer.app.presentation.settings.SettingsScreen
import com.yogatimer.app.presentation.timer.ActiveTimerScreen

/**
 * Navigation routes for the app.
 */
object NavRoutes {
    const val HOME = "home"
    const val CREATE_WORKOUT = "create_workout"
    const val EDIT_WORKOUT = "edit_workout/{workoutId}"
    const val ACTIVE_TIMER = "active_timer/{workoutId}"
    const val SETTINGS = "settings"

    fun editWorkout(workoutId: Long) = "edit_workout/$workoutId"
    fun activeTimer(workoutId: Long) = "active_timer/$workoutId"
}

/**
 * App navigation graph.
 *
 * Defines all screens and navigation paths in the app.
 *
 * @param navController Navigation controller
 */
@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        // Home Screen
        composable(NavRoutes.HOME) {
            HomeScreen(
                onWorkoutClick = { workout ->
                    navController.navigate(NavRoutes.activeTimer(workout.id))
                },
                onCreateWorkout = {
                    navController.navigate(NavRoutes.CREATE_WORKOUT)
                },
                onEditWorkout = { workout ->
                    navController.navigate(NavRoutes.editWorkout(workout.id))
                },
                onNavigateToSettings = {
                    navController.navigate(NavRoutes.SETTINGS)
                }
            )
        }

        // Create Workout Screen
        composable(NavRoutes.CREATE_WORKOUT) {
            CreateWorkoutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Edit Workout Screen (reuses CreateWorkoutScreen)
        composable(
            route = NavRoutes.EDIT_WORKOUT,
            arguments = listOf(
                navArgument("workoutId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
            CreateWorkoutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Active Timer Screen
        composable(
            route = NavRoutes.ACTIVE_TIMER,
            arguments = listOf(
                navArgument("workoutId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
            ActiveTimerScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Settings Screen
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
