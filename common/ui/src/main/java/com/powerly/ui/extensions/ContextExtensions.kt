package com.powerly.ui.extensions

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity

val Context.intent: Intent? get() = (this as? ComponentActivity)?.intent
