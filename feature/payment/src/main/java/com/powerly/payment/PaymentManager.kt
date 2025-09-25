package com.powerly.payment

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.CardParams
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.Token
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.android.stripecardscan.cardscan.CardScanSheet
import com.stripe.android.stripecardscan.cardscan.CardScanSheetResult
import com.stripe.android.stripecardscan.payment.card.ScannedCard
import org.koin.core.annotation.Single


/**
 * A class to manage payment operations with stripe sde.
 */
class PaymentManager(
    private val context: Context,
    private val publishableKey: String
) {
    private var stripe: Stripe? = null

    /**
     * Initializes the PaymentManager with app context.
     */

    init {
        if (publishableKey.isNotBlank()) {
            PaymentConfiguration.init(context, publishableKey)
            stripe = Stripe(context.applicationContext, publishableKey)
        }
    }

    companion object {
        private const val TAG = "PaymentManager"

        private var paymentLauncher: PaymentLauncher? = null
        private var nextActionCallback: ((PaymentResult) -> Unit)? = null

        private var cardScanSheet: CardScanSheet? = null
        private var scannerCallback: ((ScannedCard) -> Unit)? = null
    }

    val appContext: Context get() = this.context

    /**
     * Creates a payment token.
     *
     * @param parms The payment method creation parameters.
     * @param onSuccess The callback to be invoked on success with the created token.
     * @param onError The callback to be invoked on error with the error message.
     */
    fun createToken(
        parms: PaymentMethodCreateParams,
        onSuccess: (Token) -> Unit,
        onError: (String) -> Unit
    ) {
        val cardMap = parms.card?.toParamMap()
        Log.i(TAG, "createToken")
        val cardParams = CardParams(
            number = cardMap?.get("number").toString(),
            cvc = cardMap?.get("cvc").toString(),
            expMonth = cardMap?.get("exp_month").toString().toIntOrNull() ?: 0,
            expYear = cardMap?.get("exp_year").toString().toIntOrNull() ?: 0
        )
        Log.v(TAG, "cardParams - $cardParams")
        if (stripe != null) stripe?.createCardToken(
            cardParams = cardParams,
            callback = object : ApiResultCallback<Token> {
                override fun onError(e: Exception) {
                    val message = e.message.orEmpty()
                    onError(message)
                    e.printStackTrace()
                }

                override fun onSuccess(result: Token) {
                    Log.v(TAG, "payment-method ${result.id}")
                    onSuccess(result)
                }
            }
        ) else onError("Stripe is not initialized")
    }

    /**
     * Initializes the payment launcher for handling next actions.
     * - Call this from activity onCreate method
     *
     * @param activity  Activity to use for launching the payment flow.
     */
    fun initNextActionForPayment(activity: ComponentActivity) {
        paymentLauncher = PaymentLauncher.create(
            activity = activity,
            publishableKey = publishableKey,
            callback = { paymentResult ->
                nextActionCallback?.invoke(paymentResult)
            }
        )
    }

    /**
     * Handles the next action for a payment intent.
     *
     * @param clientSecret The client secret of the payment intent.
     * @param onResult The callback to be invoked with the payment result.
     */
    fun handleNextActionForPayment(
        clientSecret: String,
        onResult: (PaymentResult) -> Unit
    ) {
        Log.v(TAG, "handleNextActionForPayment - $clientSecret")
        nextActionCallback = onResult
        paymentLauncher?.handleNextActionForPaymentIntent(clientSecret)
    }

    /**
     * Initializes the card scanner using the provided activity and publishable key.
     *
     * This method creates a `CardScanSheet` instance and sets up a callback to handle the scanning results.
     * The callback handles three possible outcomes:
     * - `Completed`: The card was scanned successfully, and the scanned card details are passed to the `scannerCallback`.
     * - `Canceled`: The user canceled the scan operation.
     * - `Failed`: The scan operation failed due to an error.
     *
     * @param activity The Activity used to create the `CardScanSheet`.
     */
    fun initCardScanner(activity: ComponentActivity) {
        Log.v(TAG, "initCardScanner")
        cardScanSheet = CardScanSheet.create(
            from = activity,
            // stripePublishableKey = PUBLISHABLE_KEY,
            cardScanSheetResultCallback = { result ->
                when (result) {
                    is CardScanSheetResult.Completed -> {
                        val scannedCard = result.scannedCard
                        Log.i(TAG, "CardScanSheetResult.Completed - $scannedCard")
                        scannerCallback?.invoke(scannedCard)
                    }

                    is CardScanSheetResult.Canceled -> {
                        Log.e(TAG, "CardScanSheetResult.Canceled")
                    }

                    is CardScanSheetResult.Failed -> {
                        Log.e(TAG, "CardScanSheetResult.Failed")
                    }
                }
            }
        )
    }

    /**
     * Presents the card scanner sheet to the user.
     *
     * This method invokes the previously initialized `CardScanSheet` to start the card scanning process.
     * It also sets the `scannerCallback` to the provided `onResult` lambda, which will be invoked with the
     * scanned card details when the scanning process is completed successfully.
     *
     * @param onResult A lambda function that will be called with the `ScannedCard` object when the scan is successful.
     */
    fun showCardScanner(onResult: (ScannedCard) -> Unit) {
        Log.v(TAG, "showCardScanner")
        scannerCallback = onResult
        cardScanSheet?.present()
    }
}