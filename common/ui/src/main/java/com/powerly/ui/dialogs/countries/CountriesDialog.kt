package com.powerly.ui.dialogs.countries

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.powerly.core.model.location.Country
import com.powerly.ui.dialogs.MyDialogState
import com.powerly.ui.dialogs.MyScreenBottomSheet

@Composable
fun CountriesDialog(
    state: MyDialogState? = null,
    countriesList: () -> List<Country>,
    selectedCountry: () -> Country?,
    onSelectCountry: (Country) -> Unit,
    onDismiss: () -> Unit = {}
) {
    MyScreenBottomSheet(state = state, onDismiss = onDismiss) {
        val countries = remember { countriesList }
        CountriesDialogContent(
            selectedCountry = selectedCountry,
            countries = countries(),
            onSelect = {
                onSelectCountry(it)
                state?.dismiss()
                onDismiss()
            }
        )
    }
}

