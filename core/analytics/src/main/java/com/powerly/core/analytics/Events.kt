package com.powerly.core.analytics

object EVENTS {
    /**
     * WELCOME
     */

    //Welcome screen, Tap on Lets get started button
    const val WELCOME_GET_STARTED = "get_started"

    //Welcome screen, Tap on Continue as guest button
    const val WELCOME_CONTINUE_GUEST = "continue_guest"

    /**
     * LOGIN
     */

    //Register user, calling this at register api success response after user moved to verification
    const val LOGIN_REGISTER = "registration"

    //Sig In user, calling this at successfully sign in api response after use moved to verification screen
    const val LOGIN = "signin"

    //Login / Registration, Tap on Agree & Continue Button
    const val LOGIN_AGREE_CONTINUE = "agree_continue"

    /**
     * VERIFICATION
     */

    //Verification if user created account already
    const val VERIFICATION = "verification"
    const val VERIFICATION_KEY = "type"
    const val VERIFICATION_LOGIN = "login"
    const val VERIFICATION_REGISTER = "registration"
    const val VERIFICATION_HELP = "verification_help"

    /**
     * ACCOUNT INFO
     */

    //Account information screen where user fill and update profile
    const val ACCOUNT_INFORMATION_UPDATE = "account_information_update"

    //Account information screen where user skip to update profile
    const val ACCOUNT_INFORMATION_SKIP = "account_information_skip"

    /**
     * Home
     */

    //Tap action of sign-in button for guest user
    const val HOME_SIGN_IN = "sigin_tap_home"

    //Select category from home screen
    const val HOME_CATEGORY_SELECT = "select_category_home"

    //Open scan QR page
    const val HOME_QR_SCAN = "scan_qr_open"

    /**
     * MAP
     */

    //Tap action of repeat last order
    const val MAP_REPEAT_ORDER_TAP = "repeat_order_tap"


    //Select location page -> tap action to update location
    const val MAP_LOCATION_UPDATE = "update_location_tap"

    //Open Select location page from home screen
    const val MAP_LOCATION_OPEN = "select_location_open"

    //Showing popup when user tap on search location field
    const val MAP_LOCATION_SEARCH = "search_location_popup"

    /**
     * MY ORDERS
     */

    //My order screen open
    const val ORDERS_TAB_OPEN = "my_order_open"

    //Select Active orders from my order page drop down list
    const val ORDERS_ACTIVE_OPEN = "active_order_chose"

    //Select Recurring orders from my order page drop down list
    const val ORDERS_RECURRING_OPEN = "recurring_order_chose"

    //Select past orders from my order page drop down list
    const val ORDERS_PAST_OPEN = "past_order_chose"


    /**
     * ORDER PROCESS
     */

    const val PRODUCT_ID = "product_id"


    //Add products from products&services screen
    const val PRODUCTS_ADD = "add_product"

    //Remove products from products&services screen
    const val PRODUCTS_REMOVE = "remove_product"

    //Tap Continue button after selection of products
    const val PRODUCTS_CONTINUE_TAP = "products_continue_tap"

    //Change category from product list page
    const val PRODUCTS_CHANGE_CATEGORY = "change_category_products"

    //Change service type of product
    const val PRODUCTS_CHANGE_SERVICE_TYPE = "change_service_type"


    //Installation per product page open from product selection page
    const val PRODUCTS_INSTALLATION_TOGGLE = "installation_service_all_open"

    /**
     * STORE
     */

    const val STORE_ID = "store_id"

    //Detect store api returns any store then this event will be trigger.
    const val STORE_DETECT = "detect_store"

    //Add store product
    const val STORE_PRODUCT_ADD = "add_store_product"

    //Remove store product
    const val STORE_PRODUCT_REMOVE = "remove_store_product"

    //Continue button from store products page
    const val STORE_PRODUCT_CONTINUE = "store_product_continue_tap"

    //Store order confirmation screen open
    const val STORE_ORDER_CONFIRMATION_OPEN = "store_order_confirmation_open"

    //Store order created successfully
    const val STORE_ORDER_CREATED = "store_order_created"

    //Tap call button from store detail screen
    const val STORE_BUTTON_CALL = "call_tap"

    //Tap directions button from store detail scree
    const val STORE_BUTTON_DIRECTIONS = "directions_tap"

    //Tap Buy button from store detail screen
    const val STORE_BUTTON_BUY = "buy_tap"

    /**
     * TIME SLOTS
     */

    //Open Date & timeslots page
    const val TIMESLOTS_OPEN = "date_timeslots_open"

    //Open Date & timeslots page for recurring
    const val TIMESLOTS_RECURRING_OPEN = "date_timeslots_recurrinng_open"

    /**
     * CONFIRMATION
     */

    //Open Order confirmation page
    const val ORDER_CONFIRMATION_OPEN = "order_confirmation_open"

    //Edit Product page at order confirmation page
    const val ORDER_PRODUCTS_EDIT = "edit_product_open"

    //Promo code applied successfully at order confirmation page
    const val ORDER_PROMO_CODE_APPLIED = "promocode_applied"

    //Referral code applied successfully at order confirmation page
    const val ORDER_REFERRAL_APPLIED = "referral_applied"

    //Created order successfully
    const val ORDER_CREATED = "created_order"

    //Created recurring order successfully
    const val ORDER_RECURRING_CREATED = "created_recurring_order"

    //Tap on Edit time slots button at order confirmation page
    const val ORDER_TIMESLOT_EDIT = "edit_timeslot_tap"

    //Update products from recommended products at order confirmation page
    const val ORDER_PRODUCTS_RECOMMENDED_UPDATE = "update_products_reco"

    /**
     * ORDER EDIT
     */

    //When edit_order api return successful
    const val ORDER_EDITED = "edit_order"

    //when edit order button taped
    const val ORDER_EDIT_TAB = "edit_order_tap"

    //Edited recurring order successfully
    const val ORDER_RECURRING_EDITED = "edited_recurring_order"

    //Edited recurring order successfully
    const val ORDER_RECURRING_EDIT_TAB = "edit_recurring_order_tap"


    /**
     * ORDER STATUS
     */


    //Cancel order successfully
    const val ORDER_STATUS_CANCEL = "cancel_order"

    const val ORDER_STATUS_KEY = "from"
    const val ORDER_STATUS = "order_status"

    //Open Order detail page
    const val ORDER_STATUS_OPEN = "order_detail_open"

    //Tap on track order button from order detail page
    const val ORDER_STATUS_TRACK = "track_order_tap"

    //Get help press from order detail page
    const val ORDER_STATUS_GET_HELP = "get_help_tap"

    //Tap action of change payment method view
    const val ORDER_STATUS_PAYMENT_METHOD = "change_payment_method_tap"


    /**
     * FEEDBACK
     */

    //Feedback page not now success
    const val FEEDBACK_NOT_NOW = "feedback_not_now"

    //Feedback page rating successfully
    const val FEEDBACK_RATING = "feedback_rating"

    const val FEEDBACK_RATING_KEY = "rating"

    /**
     * SIDE MENU
     */

    //Sidemenu open from any screens
    const val SIDE_MENU_ACCOUNT = "account_open"


    //Notification screen open
    const val SIDE_MENU_NOTIFICATION = "notification_open"

    //Reward screen open
    const val SIDE_MENU_REWARD = "reward_view_open"


    //Profile screen open
    const val SIDE_MENU_PROFILE = "profile_open"

    //Wallet screen open
    const val SIDE_MENU_WALLET = "wallet_open"

    //Open support page from sidemenu or order status screen
    const val SIDE_MENU_SUPPORT = "support_open"

    //Invite screen open
    const val SIDE_MENU_INVITE = "invite_open"

    /**
     * SMART DEVICES
     */

    //Open Smart Devices screen
    const val SIDE_MENU_DEVICES = "smart_devices_open"

    //Created devices before configuration
    const val DEVICE_CREATED = "created_device"

    //Approved devices from notification or approved from pending approval screen
    const val DEVICE_APPROVED = "approved_device"

    /**
     * BALANCE
     */

    //Open show balance screen
    const val BALANCE_SHOW = "show_balance_open"

    //Open add balance screen
    const val BALANCE_ADD = "add_balance_open"

    //Tap On agree continue on refilll balance
    const val BALANCE_REFILL = "agree_continue_refill_balance"
}