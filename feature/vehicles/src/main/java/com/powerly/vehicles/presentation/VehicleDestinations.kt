package com.powerly.vehicles.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.powerly.core.model.powerly.Vehicle
import com.powerly.navigation.AppRoutes
import com.powerly.navigation.IRoute
import com.powerly.ui.navigation.sharedGraphViewModel
import com.powerly.vehicles.presentation.newVehicle.NewVehicleViewModel
import com.powerly.vehicles.presentation.newVehicle.VehicleAddScreen
import com.powerly.vehicles.presentation.vehicleDetails.make.MakesScreen
import com.powerly.vehicles.presentation.vehicleDetails.model.ModelScreen
import com.powerly.vehicles.presentation.vehicleDetails.options.DetailsScreen
import com.powerly.vehicles.presentation.vehicleList.VehiclesScreen

fun NavGraphBuilder.vehiclesDestinations(
    navController: NavHostController
) {
    navigation<AppRoutes.Vehicles>(startDestination = AppRoutes.Vehicles.List) {
        composable<AppRoutes.Vehicles.List> { entry ->
            val newViewModel = entry.sharedGraphViewModel<NewVehicleViewModel>(
                navController,
                parentRoute = AppRoutes.Vehicles
            )
            VehiclesScreen(
                onAddVehicle = {
                    newViewModel.showModels(true)
                    newViewModel.setVehicle(Vehicle())
                    navController.navigate(AppRoutes.Vehicles.New)
                },
                onEditVehicle = { vehicle ->
                    newViewModel.setVehicle(vehicle)
                    navController.navigate(AppRoutes.Vehicles.New)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable<AppRoutes.Vehicles.New> { entry ->
            val newViewModel = entry.sharedGraphViewModel<NewVehicleViewModel>(
                navController,
                parentRoute = AppRoutes.Vehicles
            )
            VehicleAddScreen(
                viewModel = newViewModel,
                onBack = { navController.popBackStack() },
                openManufacturer = {
                    navController.navigate(AppRoutes.Vehicles.New.Manufacturer)
                },
                openModel = {
                    navController.navigate(AppRoutes.Vehicles.New.Model)
                },
                openOptions = {
                    navController.navigate(AppRoutes.Vehicles.New.Options)
                }
            )
        }
        composable<AppRoutes.Vehicles.New.Manufacturer> { entry ->
            val newViewModel = entry.sharedGraphViewModel<NewVehicleViewModel>(
                navController,
                parentRoute = AppRoutes.Vehicles
            )
            MakesScreen(
                direction = AppRoutes.Vehicles.New.Manufacturer,
                onClose = { navController.popBackStack() },
                onNext = {
                    newViewModel.setMake(it)
                    navController.next(AppRoutes.Vehicles.New.Model)
                }
            )
        }
        composable<AppRoutes.Vehicles.New.Model> { entry ->
            val newViewModel = entry.sharedGraphViewModel<NewVehicleViewModel>(
                navController,
                parentRoute = AppRoutes.Vehicles
            )
            ModelScreen(
                makeId = newViewModel.makeId,
                direction = AppRoutes.Vehicles.New.Model,
                onClose = { navController.popBackStack() },
                onNext = {
                    newViewModel.setModel(it)
                    navController.next(AppRoutes.Vehicles.New.Options)
                }
            )
        }
        composable<AppRoutes.Vehicles.New.Options> { entry ->
            val newViewModel = entry.sharedGraphViewModel<NewVehicleViewModel>(
                navController,
                parentRoute = AppRoutes.Vehicles
            )
            DetailsScreen(
                viewModel = newViewModel,
                direction = AppRoutes.Vehicles.New.Options,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private fun NavHostController.next(destination: IRoute) {
    this.navigate(destination) {
        popUpTo(AppRoutes.Vehicles.New) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
