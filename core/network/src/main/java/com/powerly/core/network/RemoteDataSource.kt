package com.powerly.core.network


import com.powerly.core.model.location.CountriesResponse
import com.powerly.core.model.location.CountryResponse
import com.powerly.core.model.location.CurrenciesResponse
import com.powerly.core.model.payment.AddCardBody
import com.powerly.core.model.payment.BalanceRefillBody
import com.powerly.core.model.payment.BalanceRefillResponse
import com.powerly.core.model.payment.BalancesResponse
import com.powerly.core.model.payment.CardUpdateResponse
import com.powerly.core.model.payment.CardsResponse
import com.powerly.core.model.payment.WalletsResponse
import com.powerly.core.model.payment.WithdrawResponse
import com.powerly.core.model.powerly.ChargingResponse
import com.powerly.core.model.powerly.ConnectorsResponse
import com.powerly.core.model.powerly.MakesResponse
import com.powerly.core.model.powerly.MediaResponse
import com.powerly.core.model.powerly.ModelsResponse
import com.powerly.core.model.powerly.PowerSourceResponse
import com.powerly.core.model.powerly.PowerSourcesResponse
import com.powerly.core.model.powerly.ReviewAddResponse
import com.powerly.core.model.powerly.ReviewBody
import com.powerly.core.model.powerly.ReviewOptionsResponse
import com.powerly.core.model.powerly.ReviewResponse
import com.powerly.core.model.powerly.ReviewsResponse
import com.powerly.core.model.powerly.SessionDetailsResponse
import com.powerly.core.model.powerly.SessionsResponsePaginated
import com.powerly.core.model.powerly.StartChargingBody
import com.powerly.core.model.powerly.StopChargingBody
import com.powerly.core.model.powerly.VehicleAddBody
import com.powerly.core.model.powerly.VehicleAddResponse
import com.powerly.core.model.powerly.VehiclesResponse
import com.powerly.core.model.user.AuthenticationResponse
import com.powerly.core.model.user.DeviceBody
import com.powerly.core.model.user.EmailCheckBody
import com.powerly.core.model.user.EmailCheckResponse
import com.powerly.core.model.user.EmailForgetBody
import com.powerly.core.model.user.EmailForgetResponse
import com.powerly.core.model.user.EmailLoginBody
import com.powerly.core.model.user.EmailLoginResponse
import com.powerly.core.model.user.EmailRegisterBody
import com.powerly.core.model.user.EmailRegisterResponse
import com.powerly.core.model.user.EmailResetBody
import com.powerly.core.model.user.EmailVerifyResendBody
import com.powerly.core.model.user.EmailVerifyResendResponse
import com.powerly.core.model.user.LogoutBody
import com.powerly.core.model.user.RefreshTokenResponse
import com.powerly.core.model.user.SocialLoginBody
import com.powerly.core.model.user.UpdateDeviceResponse
import com.powerly.core.model.user.UserResponse
import com.powerly.core.model.user.UserUpdateBody
import com.powerly.core.model.user.VerificationBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

private object ApiEndPoints {

    const val COUNTRIES = "countries"
    const val COUNTRY = "countries/{id}"
    const val COUNTRIES_CURRENCIES = "countries/currencies"
    const val DEVICE = "device"

    const val PAYMENT_CARDS = "payment-cards"
    const val PAYMENT_CARD_DEFAULT = "payment-cards/default/{id}"
    const val PAYMENT_CARD_DELETE = "payment-cards/{id}"


    const val BALANCE_OFFERS = "balance/offers"
    const val BALANCE_REFILL = "balance/top-up"

    const val PAYOUTS = "payouts"
    const val PAYOUTS_REQUEST = "payouts/request-payout"

    /**
     * Authentication & User
     */

    const val USER_ME = "users/me"

    const val AUTH_TOKEN_REFRESH = "auth/token/refresh"
    const val AUTH_CHECK = "auth/check"
    const val AUTH_PASSWORD_FORGET = "auth/password/forgot"
    const val AUTH_PASSWORD_RESET = "auth/password/reset"
    const val AUTH_PASSWORD_RESET_RESEND = "auth/password/reset/resend"
    const val AUTH_PASSWORD_RESET_VERIFY = "auth/password/reset/verify"
    const val AUTH_EMAIL_CHECK = "auth/email/check"
    const val AUTH_EMAIL_LOGIN = "auth/login"
    const val AUTH_EMAIL_REGISTER = "auth/register"
    const val AUTH_EMAIL_VERIFY = "auth/email/verify"
    const val AUTH_EMAIL_VERIFY_RESEND = "auth/resend-verification"
    const val AUTH_GOOGLE = "auth/google"
    const val AUTH_HUAWEI = "auth/huawei"
    const val AUTH_LOGOUT = "auth/logout"

    /**
     * Powerly
     */

    const val POWER_SOURCE = "stations"
    const val POWER_SOURCE_ACTION = "stations/{id}"
    const val POWER_SOURCE_MEDIA = "stations/{id}/media"
    const val POWER_SOURCE_REVIEWS = "stations/{id}/reviews"
    const val POWER_SOURCE_TOKEN = "stations/token/{identifier}"

    const val POWER_SOURCE_ORDER_DETAILS = "orders/{orderId}"
    const val POWER_SOURCE_ORDERS = "orders"
    const val POWER_SOURCE_CHARGING_STOP = "orders/stop"

    const val REVIEWS = "reviews"
    const val REVIEW_OPTIONS = "reviews/options"
    const val REVIEW_SKIP = "orders/{order_id}/review/skip"
    const val REVIEW_ADD = "orders/{order_id}/review"

    const val VEHICLES = "vehicles"
    const val VEHICLES_ACTION = "vehicles/{id}"
    const val VEHICLE_MAKES = "vehicles/makes"
    const val VEHICLE_MODELS = "vehicles/models/{make_id}"
    const val VEHICLE_CONNECTORS = "vehicles/connectors"

}


interface RemoteDataSource {

    @GET(ApiEndPoints.COUNTRIES)
    suspend fun getCountries(): CountriesResponse

    @GET(ApiEndPoints.COUNTRIES_CURRENCIES)
    suspend fun getCurrencies(): CurrenciesResponse

    @GET(ApiEndPoints.COUNTRY)
    suspend fun getCountry(@Path("id") id: Int): CountryResponse

    @Headers("Content-Type: application/json")
    @PUT(ApiEndPoints.DEVICE)
    suspend fun updateDevice(
        @Body body: DeviceBody
    ): UpdateDeviceResponse


    /**
     * Authentication
     */

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.AUTH_LOGOUT)
    suspend fun authLogout(@Body request: LogoutBody): Response<Any>

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.AUTH_TOKEN_REFRESH)
    suspend fun refreshToken(): RefreshTokenResponse

    /////////email
    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.AUTH_EMAIL_CHECK)
    suspend fun emailCheck(
        @Body request: EmailCheckBody,
    ): EmailCheckResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.AUTH_EMAIL_LOGIN)
    suspend fun emailLogin(
        @Body request: EmailLoginBody
    ): EmailLoginResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.AUTH_EMAIL_REGISTER)
    suspend fun emailRegister(
        @Body request: EmailRegisterBody
    ): EmailRegisterResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.AUTH_EMAIL_VERIFY)
    suspend fun emailVerify(
        @Body request: VerificationBody
    ): AuthenticationResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.AUTH_EMAIL_VERIFY_RESEND)
    suspend fun emailVerifyResend(
        @Body request: EmailVerifyResendBody
    ): EmailVerifyResendResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.AUTH_PASSWORD_FORGET)
    suspend fun emailPasswordForget(
        @Body request: EmailForgetBody
    ): EmailForgetResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.AUTH_PASSWORD_RESET)
    suspend fun emailPasswordReset(
        @Body request: EmailResetBody
    ): AuthenticationResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.AUTH_PASSWORD_RESET_RESEND)
    suspend fun emailPasswordResetResend(
        @Body request: EmailVerifyResendBody
    ): EmailVerifyResendResponse


    ///////////////////// device account
    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.AUTH_GOOGLE)
    suspend fun socialGoogleLogin(
        @Body request: SocialLoginBody
    ): AuthenticationResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.AUTH_HUAWEI)
    suspend fun socialHuaweiLogin(
        @Body request: SocialLoginBody
    ): AuthenticationResponse


    /**
     * User
     */

    @Headers("Content-Type: application/json")
    @DELETE(ApiEndPoints.USER_ME)
    suspend fun deleteUser(): Response<Any>

    @Headers("Content-Type: application/json")
    @PUT(ApiEndPoints.USER_ME)
    suspend fun updateUser(
        @Body request: UserUpdateBody
    ): UserResponse

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.USER_ME)
    suspend fun getUser(): UserResponse

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.USER_ME)
    suspend fun authCheck(): Response<UserResponse>

    /**
     * Power sources
     */

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.POWER_SOURCE_ACTION)
    suspend fun getPowerSource(
        @Path("id") id: String
    ): PowerSourceResponse


    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.POWER_SOURCE)
    suspend fun getNearPowerSources(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("search") search: String?
    ): PowerSourcesResponse


    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.POWER_SOURCE_MEDIA)
    suspend fun getMedia(
        @Path("id") id: String
    ): MediaResponse

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.POWER_SOURCE_REVIEWS)
    suspend fun getReviews(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int = 15
    ): ReviewResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.POWER_SOURCE_ORDERS)
    suspend fun startCharging(
        @Body body: StartChargingBody
    ): ChargingResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.POWER_SOURCE_CHARGING_STOP)
    suspend fun stopCharging(
        @Body body: StopChargingBody
    ): ChargingResponse


    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.POWER_SOURCE_TOKEN)
    suspend fun powerSourceDetails(
        @Path("identifier") identifier: String
    ): PowerSourceResponse


    /**
     * Sessions
     */
    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.POWER_SOURCE_ORDER_DETAILS)
    suspend fun sessionDetails(
        @Path("orderId") orderId: String
    ): SessionDetailsResponse


    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.POWER_SOURCE_ORDERS)
    suspend fun getActiveOrders(
        @Query("page") page: Int,
        @Query("status") status: String = "active",
        @Query("limit") limit: Int = 15
    ): SessionsResponsePaginated

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.POWER_SOURCE_ORDERS)
    suspend fun getCompleteOrders(
        @Query("page") page: Int,
        @Query("status") status: String = "complete",
        @Query("limit") limit: Int = 15
    ): SessionsResponsePaginated

    /**
     * FEEDBACK
     */

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.REVIEW_OPTIONS)
    suspend fun reviewOptions(): ReviewOptionsResponse

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.REVIEWS)
    suspend fun reviewPending(
        @Query("limit") limit: Int
    ): ReviewsResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.REVIEW_ADD)
    suspend fun reviewAdd(
        @Path("order_id") orderId: String,
        @Body body: ReviewBody
    ): ReviewAddResponse

    @POST(ApiEndPoints.REVIEW_SKIP)
    suspend fun reviewSkip(
        @Path("order_id") orderId: String
    ): ReviewAddResponse

    /**
     * Vehicles
     */

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.VEHICLES)
    suspend fun vehicleAdd(@Body request: VehicleAddBody): VehicleAddResponse

    @Headers("Content-Type: application/json")
    @PUT(ApiEndPoints.VEHICLES_ACTION)
    suspend fun vehicleUpdate(
        @Path("id") id: Int,
        @Body request: VehicleAddBody
    ): VehicleAddResponse

    @Headers("Content-Type: application/json")
    @DELETE(ApiEndPoints.VEHICLES_ACTION)
    suspend fun vehicleDelete(@Path("id") id: Int): VehicleAddResponse

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.VEHICLES)
    suspend fun vehiclesList(): VehiclesResponse

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.VEHICLE_MAKES)
    suspend fun vehiclesMakes(): MakesResponse

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.VEHICLE_MODELS)
    suspend fun vehiclesModels(
        @Path("make_id") makeId: Int? = null
    ): ModelsResponse

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.VEHICLE_CONNECTORS)
    suspend fun vehiclesConnectors(
    ): ConnectorsResponse

    /**
     * Payment
     */

    @Headers("Content-Type: application/json")
    @GET(ApiEndPoints.PAYMENT_CARDS)
    suspend fun cardList(): CardsResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.PAYMENT_CARDS)
    suspend fun cardAdd(@Body request: AddCardBody): CardUpdateResponse

    @Headers("Content-Type: application/json")
    @POST(ApiEndPoints.PAYMENT_CARD_DEFAULT)
    suspend fun cardDefault(@Path("id") cardId: String): CardUpdateResponse

    @Headers("Content-Type: application/json")
    @DELETE(ApiEndPoints.PAYMENT_CARD_DELETE)
    suspend fun cardDelete(@Path("id") cardId: String): CardUpdateResponse

    @POST(ApiEndPoints.BALANCE_REFILL)
    suspend fun refillBalance(
        @Body request: BalanceRefillBody
    ): BalanceRefillResponse

    @GET(ApiEndPoints.BALANCE_OFFERS)
    suspend fun getBalanceItems(
        @Query("country_id") countryId: Int?
    ): BalancesResponse

    @GET(ApiEndPoints.PAYOUTS)
    suspend fun walletList(): WalletsResponse

    @POST(ApiEndPoints.PAYOUTS_REQUEST)
    suspend fun walletPayout(): WithdrawResponse
}