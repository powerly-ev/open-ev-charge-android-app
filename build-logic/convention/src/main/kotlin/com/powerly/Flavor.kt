package com.powerly

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

// The content for the app can either come from local static data which is useful for demo
// purposes, or from a test or production backend server which supplies up-to-date, real content.
@Suppress("EnumEntryName")
enum class MyFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    default(FlavorDimension.contentType, applicationIdSuffix = null),
    gms(FlavorDimension.contentType, applicationIdSuffix = null),
    //demo(FlavorDimension.contentType, applicationIdSuffix = null)
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: MyFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            MyFlavor.values().forEach { myFlavor ->
                register(myFlavor.name) {
                    dimension = myFlavor.dimension.name
                    flavorConfigurationBlock(this, myFlavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (myFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = myFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}
