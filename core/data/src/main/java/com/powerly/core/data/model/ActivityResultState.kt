package com.powerly.core.data.model

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult

class ActivityResultState(
    val startActivity: (
        packageName: String,
        onResult: ((ActivityResult?) -> Unit)?
    ) -> Unit,
    val startActivityWithBundle: (
        packageName: String,
        bundle: Bundle?,
        onResult: ((ActivityResult?) -> Unit)?
    ) -> Unit,
    val startActivityForResult: (
        intent: Intent,
        onResult: ((ActivityResult?) -> Unit)?
    ) -> Unit,
)

class PermissionsState(
    val requestSpecific: (String, onResult: ((granted: Boolean) -> Unit)?) -> Unit,
    val request: (onResult: ((granted: Boolean) -> Unit)?) -> Unit,
    val requestManually: (onResult: ((granted: Boolean) -> Unit)?) -> Unit,
    val isGranted: () -> Boolean,
    val isItGranted: (String) -> Boolean
)