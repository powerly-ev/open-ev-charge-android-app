package com.powerly.ui.theme

import androidx.compose.ui.graphics.Color

object MyColors {
    val blue = Color(0xFF008CE9)
    val green = Color(0xFF32984c)
    val greenLight = Color(0xFF7DBD00)
    val blueLight = Color(0xFFCBE0ED)
    val blueLight2 = Color(0xFFA4D0F2)

    val grey900 = Color(0xFF222222)
    val grey800 = Color(0xFF414141)
    val grey700 = Color(0xFF666666)
    val grey600 = Color(0xFF7A7A7A)
    val grey500 = Color(0xFF999999)
    val grey250 = Color(0xFFD4D4D4)
    val grey200 = Color(0xFFE8E8E8)
    val grey100 = Color(0xFFF3F3F3)
    val grey50 = Color(0xFFF8F8F8)

    val buttonColor = grey100
    val dividerColor = grey200
    val background = Color(0xFFE5E5E5)
    val subColor = grey500
    val viewColor = grey50
    val viewColor2 = grey100
    val viewColor3 = grey200
    val borderColor = grey200
    val disabledColor = grey100
    val white = Color(0xFFFFFFFF)
    val red = Color(0xFFDE2929)
    val red500: Color = Color(0xFFE6352B)
    val redLight: Color = Color(0xFFFFF2F2)
    val errorColor: Color = Color(0xFFE6352B)
    val huaweiColor: Color = Color(0xFFBF2930)

    object OrderStatus {
        val InCart = Color(0xFFDE2929)
        val Open = Color(0xFF7A7A7A)
        val Confirmed = Color(0xFF008CE9)
        val OnTheWay = Color(0xFFF79E1B)
        val Delivered = Color(0xFF7DBD00)
        val Cancelled = Color(0xFFFF423E)
    }
}


// color guide
object ColorGuide1 {
    val primary = MyColors.blue
    val onPrimary = MyColors.white
    val secondary = MyColors.grey900
    val onSecondary = MyColors.white
    val tertiary = MyColors.subColor
    val onTertiary = secondary
    var background = MyColors.grey50
    val onBackground = MyColors.grey900
    val surface = MyColors.grey50
    val onSurface = MyColors.grey900
    val errorColor = MyColors.errorColor
}


