package com.powerly.lib

import androidx.navigation.NavHostController
import com.powerly.core.model.powerly.OrderTab
import com.powerly.core.model.util.Message
import kotlinx.serialization.Serializable

@Serializable
open class IRoute(var page: Int = 0, var total: Int = 0) : Route()

@Serializable
open class Route

object AppRoutes {
    @Serializable
    data class MessageDialog(
        private val message: String,
        private val isError: Boolean
    ) : Route() {
        val asMessage: Message
            get() = Message(
                msg = message,
                type = when {
                    isError -> Message.ERROR
                    else -> Message.SUCCESS
                }
            )
    }

    @Serializable
    data object Splash : Route() {
        @Serializable
        data object UpdateApp : Route()
    }

    @Serializable
    data object Navigation : Route() {
        @Serializable
        data object Home : Route() {
            @Serializable
            data object Map : Route()
        }

        @Serializable
        data class Orders(val type: Int = OrderTab.ACTIVE) : Route() {
            @Serializable
            data object Details
        }

        @Serializable
        data object Scan : Route()

        @Serializable
        data object Account : Route()
    }

    @Serializable
    data object PowerSource : Route() {
        @Serializable
        data class Details(val id: String) : Route()

        @Serializable
        data object Media

        @Serializable
        data class Reviews(val id: String)

        @Serializable
        data object OnBoarding

        @Serializable
        data object ChargingDialog

        @Serializable
        data class Charging(
            val chargePointId: String,
            val quantity: String,
            val connector: Int?,
            val orderId: String
        )

        @Serializable
        data class Feedback(
            val sessionId: String,
            val title: String
        ) : Route()
    }

    @Serializable
    data object Account : Route() {
        @Serializable
        data object Invite : Route()

        @Serializable
        data object Profile : Route()

        @Serializable
        data object Language : Route()
    }

    @Serializable
    data object Payment : Route() {
        @Serializable
        data object Methods

        @Serializable
        data object Add

        @Serializable
        data object Wallet : Route()

        @Serializable
        data object Balance {
            @Serializable
            data object Add

            @Serializable
            data object Show

            @Serializable
            data object Withdraw
        }
    }

    @Serializable
    data object Vehicles : Route() {
        @Serializable
        data object List : Route()

        @Serializable
        data object New {
            @Serializable
            data object Manufacturer : IRoute(1)

            @Serializable
            data object Model : IRoute(2)

            @Serializable
            data object Options : IRoute(3)
        }
    }

    @Serializable
    data object User : Route() {
        @Serializable
        data object Welcome : Route() {
            @Serializable
            data object Language : Route()
        }

        @Serializable
        data class Agreement(val type: Int) : Route()

        data object Email : Route() {
            @Serializable
            data object Login : Route()

            @Serializable
            data object Verification : Route()

            @Serializable
            data object Country : Route()

            object Password {
                @Serializable
                data object Enter : Route()

                @Serializable
                data object Create : Route()

                @Serializable
                object Reset : Route()
            }
        }
    }
}

fun NavHostController.showMessage(message: Message) {
    this.navigate(AppRoutes.MessageDialog(message.msg, message.isError))
}

fun NavHostController.showMessage(message: String, isError: Boolean = false) {
    this.navigate(AppRoutes.MessageDialog(message, isError))
}