package com.powerly.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.resources.R


val myBorder = BorderStroke(1.dp, color = MyColors.borderColor)


/**
 * Body - Regular - 12,14,16
 */
private val bodySmall = TextStyle(
    color = MyColors.grey900,
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal
)

private val bodyMedium = bodySmall.copy(
    fontSize = 14.sp,
)

private val bodyLarge = bodySmall.copy(
    fontSize = 16.sp,
)

/**
 * Label - Medium - 11,12,14
 */

private val labelSmall = TextStyle(
    color = MyColors.grey900,
    fontSize = 11.sp,
    fontWeight = FontWeight.Medium
)
private val labelMedium = labelSmall.copy(
    fontSize = 12.sp,
)
private val labelLarge = labelSmall.copy(
    fontSize = 14.sp,
)

/**
 * Label - Medium - 16,18,22
 */

private val titleSmall = TextStyle(
    color = MyColors.grey900,
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium
)
private val titleMedium = titleSmall.copy(
    fontSize = 18.sp,
)
private val titleLarge = titleSmall.copy(
    fontSize = 22.sp,
)


// Set of Material typography styles to start with
fun myTypography(isArabic: Boolean): Typography {
    val fontFamily = Fonts.appFont(isArabic)
    return Typography(
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily)
    )
}

object Fonts {

    private val arabicFont = FontFamily(
        Font(R.font.font_arabic_light, FontWeight.Light),
        Font(R.font.font_arabic_regular, FontWeight.Normal),
        Font(R.font.font_arabic_regular, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.font_arabic_medium, FontWeight.Medium),
        Font(R.font.font_arabic_bold, FontWeight.Bold)
    )


    private val latinFont = FontFamily(
        Font(R.font.font_latin_thin, FontWeight.Thin),
        Font(R.font.font_latin_light, FontWeight.Light),
        Font(R.font.font_latin_regular, FontWeight.Normal),
        Font(R.font.font_latin_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.font_latin_medium, FontWeight.Medium),
        Font(R.font.font_latin_bold, FontWeight.Bold),
        Font(R.font.font_latin_black, FontWeight.Black),
    )

    fun appFont(isArabic: Boolean): FontFamily {
        return if (isArabic) arabicFont else latinFont
    }
}