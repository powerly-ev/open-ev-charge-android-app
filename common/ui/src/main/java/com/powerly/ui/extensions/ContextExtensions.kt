package com.powerly.ui.extensions

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.net.toUri

val Context.requireActivity: ComponentActivity get() = this as ComponentActivity

val Context.intent: Intent get() = this.requireActivity.intent

fun Context.finish() {
    this.requireActivity.finish()
}

fun Context.safeStartActivity(intent: Intent) {
    try {
        this.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.openCall(contact: String?) {
    if (contact.isNullOrEmpty()) return
    val phone = Intent(Intent.ACTION_DIAL, "tel:$contact".toUri())
    this.safeStartActivity(phone)
}
