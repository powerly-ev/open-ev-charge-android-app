package com.powerly.core.model.util

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