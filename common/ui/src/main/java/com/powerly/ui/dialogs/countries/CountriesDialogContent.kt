package com.powerly.ui.dialogs.countries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.core.model.location.Country
import com.powerly.resources.R
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.components.MySearchBox
import com.powerly.ui.dialogs.ItemOptionsMenu
import com.powerly.ui.theme.AppTheme

/**
 * please start interactive mode to show bottom sheet preview
 */
@Preview
@Composable
private fun AppLanguageDialogPreview() {
    val countries = listOf(
        Country(1, name = "Egypt"),
        Country(2, name = "Jordan"),
        Country(3, name = "India"),
        Country(4, name = "Russia"),
        Country(5, name = "France")
    )
    AppTheme {
        CountriesDialogContent(
            selectedCountry = { countries[0] },
            countries = countries,
            onSelect = {}
        )
    }
}


@Composable
internal fun CountriesDialogContent(
    selectedCountry: () -> Country?,
    countries: List<Country>,
    onSelect: (Country) -> Unit
) {
    var selected by remember { mutableStateOf(selectedCountry()) }
    var query by remember { mutableStateOf("") }
    val filteredCountries by remember(query) {
        derivedStateOf {
            if (query.isBlank()) {
                countries
            } else {
                countries.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }
        }
    }

    MyScreen(
        modifier = Modifier.fillMaxHeight(0.95f),
        background = Color.White,
        header = {
            MySearchBox(
                showKeyboard = false,
                showDivider = true,
                cornerRadius = 0.dp,
                hint = R.string.login_select_country,
                background = Color.White,
                iconColor = MaterialTheme.colorScheme.primary,
                afterQueryChanges = { query = it }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredCountries) { country ->
                ItemOptionsMenu(
                    title = country.name,
                    image = country.image,
                    selected = { selected?.id == country.id },
                    onSelect = {
                        selected = country
                        onSelect(country)
                    }
                )
            }
        }
    }
}