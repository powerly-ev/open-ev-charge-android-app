package com.powerly.ui.dialogs.countries

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.powerly.core.model.location.Country
import com.powerly.lib.managers.CountryManager
import com.powerly.ui.dialogs.MyScreenBottomSheet

@Composable
fun CountriesDialog(
    selectedCountry: () -> Country?,
    onSelectCountry: (Country) -> Unit,
    onDismiss: () -> Unit
) {
    MyScreenBottomSheet(onDismiss = onDismiss) {
        val countries = remember { CountryManager.getCountryList() }
        CountriesDialogContent(
            selectedCountry = selectedCountry,
            countries = countries,
            onSelect = {
                onSelectCountry(it)
                onDismiss()
            }
        )
    }
}

