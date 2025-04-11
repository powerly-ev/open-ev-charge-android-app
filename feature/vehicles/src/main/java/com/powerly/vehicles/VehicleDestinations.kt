package com.powerly.vehicles

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.powerly.lib.AppRoutes
import com.powerly.lib.IRoute
import com.powerly.vehicles.newVehicle.VehicleAddScreen
import com.powerly.vehicles.vehicleDetails.make.MakesScreen
import com.powerly.vehicles.vehicleDetails.model.ModelScreen
import com.powerly.vehicles.vehicleDetails.options.DetailsScreen
import com.powerly.vehicles.vehicleList.VehiclesScreen

fun NavGraphBuilder.vehiclesDestinations(
    navController: NavHostController,
    viewModel: VehiclesViewModel
) {
    navigation<AppRoutes.Vehicles>(startDestination = AppRoutes.Vehicles.List) {
        composable<AppRoutes.Vehicles.List> {
            VehiclesScreen(
                viewModel = viewModel,
                addNewVehicle = { navController.navigate(AppRoutes.Vehicles.New) },
                onBack = { navController.popBackStack() }
            )
        }
        composable<AppRoutes.Vehicles.New> {
            VehicleAddScreen(
                viewModel = viewModel,
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
        composable<AppRoutes.Vehicles.New.Manufacturer> {
            val viewModel = viewModel
            MakesScreen(
                viewModel = viewModel,
                direction = AppRoutes.Vehicles.New.Manufacturer,
                onClose = { navController.popBackStack() },
                onNext = {
                    viewModel.setMake(it)
                    navController.next(AppRoutes.Vehicles.New.Model)
                }
            )
        }
        composable<AppRoutes.Vehicles.New.Model> {
            val viewModel = viewModel
            ModelScreen(
                viewModel = viewModel,
                direction = AppRoutes.Vehicles.New.Model,
                onClose = { navController.popBackStack() },
                onNext = {
                    viewModel.setModel(it)
                    navController.next(AppRoutes.Vehicles.New.Options)
                }
            )
        }
        composable<AppRoutes.Vehicles.New.Options> {
            DetailsScreen(
                viewModel = viewModel,
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
