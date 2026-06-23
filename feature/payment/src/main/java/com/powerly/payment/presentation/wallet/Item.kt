package com.powerly.payment.presentation.wallet

/**
 * UI list-item helper used by the wallet options menu.
 *
 * Note: `id: Any` because callers stuff different identifier types in here. If you
 * end up reusing this widely, replace with a typed `id`.
 */
data class Item(
    val label: String,
    val img: String = "",
    var selected: Boolean = false,
    var id: Any,
) {
    override fun toString(): String {
        return "label: $label, img: $img, selected: $selected"
    }
}
