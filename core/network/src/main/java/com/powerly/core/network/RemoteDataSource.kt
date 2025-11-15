package com.powerly.core.network

import com.powerly.core.model.api.ApiResponse
import com.powerly.core.model.api.BaseResponsePaginated
import com.powerly.core.model.location.AppCurrency
import com.powerly.core.model.location.Country
import com.powerly.core.model.payment.AddCardBody
import com.powerly.core.model.payment.BalanceItem
import com.powerly.core.model.payment.BalanceRefill
import com.powerly.core.model.payment.BalanceRefillBody
import com.powerly.core.model.payment.StripCard
import com.powerly.core.model.payment.Wallet
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.Media
import com.powerly.core.model.powerly.PowerSource
import com.powerly.core.model.powerly.Review
import com.powerly.core.model.powerly.ReviewBody
import com.powerly.core.model.powerly.Session
import com.powerly.core.model.powerly.StartChargingBody
import com.powerly.core.model.powerly.StopChargingBody
import com.powerly.core.model.powerly.Vehicle
import com.powerly.core.model.powerly.VehicleAddBody
import com.powerly.core.model.powerly.VehicleMaker
import com.powerly.core.model.powerly.VehicleModel
import com.powerly.core.model.user.DeviceBody
import com.powerly.core.model.user.EmailCheck
import com.powerly.core.model.user.EmailCheckBody
import com.powerly.core.model.user.EmailForgetBody
import com.powerly.core.model.user.EmailLoginBody
import com.powerly.core.model.user.EmailRegisterBody
import com.powerly.core.model.user.EmailResetBody
import com.powerly.core.model.user.EmailVerifyResendBody
import com.powerly.core.model.user.LogoutBody
import com.powerly.core.model.user.RefreshToken
import com.powerly.core.model.user.SocialLoginBody
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserUpdateBody
import com.powerly.core.model.user.UserVerification
import com.powerly.core.model.user.VerificationBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RemoteDataSource(private val client: HttpClient) {

    suspend fun getCountries(): ApiResponse<List<Country>> {
        return client.get(ApiEndPoints.COUNTRIES).body()
    }

    suspend fun getCurrencies(): ApiResponse<List<AppCurrency>> {
        return client.get(ApiEndPoints.COUNTRIES_CURRENCIES).body()
    }

    suspend fun getCountry(id: Int): ApiResponse<Country> {
        val endpoint = ApiEndPoints.COUNTRY.replace("{id}", id.toString())
        return client.get(endpoint).body()
    }

    suspend fun updateDevice(body: DeviceBody): HttpResponse {
        return client.put(ApiEndPoints.DEVICE) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    // Authentication
    suspend fun authLogout(request: LogoutBody): HttpResponse {
        return client.post(ApiEndPoints.AUTH_LOGOUT) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun refreshToken(): ApiResponse<RefreshToken> {
        return client.get(ApiEndPoints.AUTH_TOKEN_REFRESH).body()
    }

    suspend fun emailCheck(request: EmailCheckBody): ApiResponse<EmailCheck?> {
        return client.post(ApiEndPoints.AUTH_EMAIL_CHECK) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun emailLogin(request: EmailLoginBody): HttpResponse {
        return client.post(ApiEndPoints.AUTH_EMAIL_LOGIN) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun emailRegister(request: EmailRegisterBody): ApiResponse<User?> {
        return client.post(ApiEndPoints.AUTH_EMAIL_REGISTER) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun emailVerify(request: VerificationBody): ApiResponse<User?> {
        return client.post(ApiEndPoints.AUTH_EMAIL_VERIFY) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun emailVerifyResend(request: EmailVerifyResendBody): ApiResponse<UserVerification?> {
        return client.post(ApiEndPoints.AUTH_EMAIL_VERIFY_RESEND) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun emailPasswordForget(request: EmailForgetBody): ApiResponse<UserVerification?> {
        return client.post(ApiEndPoints.AUTH_PASSWORD_FORGET) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun emailPasswordReset(request: EmailResetBody): ApiResponse<User?> {
        return client.post(ApiEndPoints.AUTH_PASSWORD_RESET) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun emailPasswordResetResend(request: EmailVerifyResendBody): ApiResponse<UserVerification?> {
        return client.post(ApiEndPoints.AUTH_PASSWORD_RESET_RESEND) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // Device account social
    suspend fun socialGoogleLogin(request: SocialLoginBody): ApiResponse<User?> {
        return client.post(ApiEndPoints.AUTH_GOOGLE) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun socialHuaweiLogin(request: SocialLoginBody): ApiResponse<User?> {
        return client.post(ApiEndPoints.AUTH_HUAWEI) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // User
    suspend fun deleteUser(): HttpResponse {
        return client.delete(ApiEndPoints.USER_ME)
    }

    suspend fun updateUser(request: UserUpdateBody): ApiResponse<User> {
        return client.put(ApiEndPoints.USER_ME) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getUser(): ApiResponse<User> {
        return client.get(ApiEndPoints.USER_ME).body()
    }

    suspend fun authCheck(): HttpResponse {
        return client.get(ApiEndPoints.USER_ME)
    }

    // Power sources
    suspend fun getPowerSource(id: String): ApiResponse<PowerSource?> {
        val endpoint = ApiEndPoints.POWER_SOURCE_ACTION.replace("{id}", id)
        return client.get(endpoint).body()
    }

    suspend fun getNearPowerSources(
        latitude: Double,
        longitude: Double,
        search: String?
    ): BaseResponsePaginated<PowerSource> {
        return client.get(ApiEndPoints.POWER_SOURCE) {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            if (search != null) parameter("search", search)
        }.body()
    }

    suspend fun getMedia(id: String): ApiResponse<List<Media>?> {
        val endpoint = ApiEndPoints.POWER_SOURCE_MEDIA.replace("{id}", id)
        return client.get(endpoint).body()
    }

    suspend fun getReviews(
        id: String, page: Int, limit: Int = 15
    ): BaseResponsePaginated<Review> {
        val endpoint = ApiEndPoints.POWER_SOURCE_REVIEWS.replace("{id}", id)
        return client.get(endpoint) {
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    suspend fun startCharging(body: StartChargingBody): ApiResponse<Session?> {
        return client.post(ApiEndPoints.POWER_SOURCE_ORDERS) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    suspend fun stopCharging(body: StopChargingBody): ApiResponse<Session?> {
        return client.post(ApiEndPoints.POWER_SOURCE_CHARGING_STOP) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    suspend fun powerSourceDetails(identifier: String): ApiResponse<PowerSource?> {
        val endpoint = ApiEndPoints.POWER_SOURCE_TOKEN.replace("{identifier}", identifier)
        return client.get(endpoint).body()
    }

    // Sessions
    suspend fun sessionDetails(orderId: String): ApiResponse<Session?> {
        val endpoint = ApiEndPoints.POWER_SOURCE_ORDER_DETAILS.replace("{orderId}", orderId)
        return client.get(endpoint).body()
    }

    suspend fun getActiveOrders(
        page: Int,
        status: String = "active",
        limit: Int = 15
    ): BaseResponsePaginated<Session> {
        return client.get(ApiEndPoints.POWER_SOURCE_ORDERS) {
            parameter("page", page)
            parameter("status", status)
            parameter("limit", limit)
        }.body()
    }

    suspend fun getCompleteOrders(
        page: Int,
        status: String = "complete",
        limit: Int = 15
    ): BaseResponsePaginated<Session> {
        return client.get(ApiEndPoints.POWER_SOURCE_ORDERS) {
            parameter("page", page)
            parameter("status", status)
            parameter("limit", limit)
        }.body()
    }

    // Feedback
    suspend fun reviewOptions(): ApiResponse<Map<String, List<String>>?> {
        return client.get(ApiEndPoints.REVIEW_OPTIONS).body()
    }

    suspend fun reviewPending(limit: Int): ApiResponse<List<Session>?> {
        return client.get(ApiEndPoints.REVIEWS) {
            parameter("limit", limit)
        }.body()
    }

    suspend fun reviewAdd(orderId: String, body: ReviewBody): ApiResponse<Session?> {
        val endpoint = ApiEndPoints.REVIEW_ADD.replace("{order_id}", orderId)
        return client.post(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    suspend fun reviewSkip(orderId: String): ApiResponse<Session?> {
        val endpoint = ApiEndPoints.REVIEW_SKIP.replace("{order_id}", orderId)
        return client.post(endpoint).body()
    }

    // Vehicles
    suspend fun vehicleAdd(request: VehicleAddBody): ApiResponse<Vehicle?> {
        return client.post(ApiEndPoints.VEHICLES) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun vehicleUpdate(id: Int, request: VehicleAddBody): ApiResponse<Vehicle?> {
        val endpoint = ApiEndPoints.VEHICLES_ACTION.replace("{id}", id.toString())
        return client.put(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun vehicleDelete(id: Int): ApiResponse<Vehicle?> {
        val endpoint = ApiEndPoints.VEHICLES_ACTION.replace("{id}", id.toString())
        return client.delete(endpoint).body()
    }

    suspend fun vehiclesList(): ApiResponse<List<Vehicle>?> {
        return client.get(ApiEndPoints.VEHICLES).body()
    }

    suspend fun vehiclesMakes(): ApiResponse<List<VehicleMaker>?> {
        return client.get(ApiEndPoints.VEHICLE_MAKES).body()
    }

    suspend fun vehiclesModels(makeId: Int? = null): ApiResponse<List<VehicleModel>?> {
        val endpoint = if (makeId != null) {
            ApiEndPoints.VEHICLE_MODELS.replace("{make_id}", makeId.toString())
        } else {
            ApiEndPoints.VEHICLE_MODELS
        }
        return client.get(endpoint).body()
    }

    suspend fun vehiclesConnectors(): ApiResponse<List<Connector>?> {
        return client.get(ApiEndPoints.VEHICLE_CONNECTORS).body()
    }

    // Payment
    suspend fun cardList(): ApiResponse<List<StripCard>> {
        return client.get(ApiEndPoints.PAYMENT_CARDS).body()
    }

    suspend fun cardAdd(request: AddCardBody): ApiResponse<List<StripCard>?> {
        return client.post(ApiEndPoints.PAYMENT_CARDS) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun cardDefault(cardId: String): ApiResponse<StripCard?> {
        val endpoint = ApiEndPoints.PAYMENT_CARD_DEFAULT.replace("{id}", cardId)
        return client.post(endpoint).body()
    }

    suspend fun cardDelete(cardId: String): ApiResponse<StripCard?> {
        val endpoint = ApiEndPoints.PAYMENT_CARD_DELETE.replace("{id}", cardId)
        return client.delete(endpoint).body()
    }

    suspend fun refillBalance(request: BalanceRefillBody): ApiResponse<BalanceRefill> {
        return client.post(ApiEndPoints.BALANCE_REFILL) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getBalanceItems(countryId: Int?): ApiResponse<List<BalanceItem>> {
        return client.get(ApiEndPoints.BALANCE_OFFERS) {
            if (countryId != null) parameter("country_id", countryId)
        }.body()
    }

    suspend fun walletList(): ApiResponse<List<Wallet>> {
        return client.get(ApiEndPoints.PAYOUTS).body()
    }

    suspend fun walletPayout(): ApiResponse<*> {
        return client.post(ApiEndPoints.PAYOUTS_REQUEST).body()
    }

    companion object {
        private const val TAG = "NewRemoteDataSource"
    }
}