package com.powerly.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.powerly.core.model.location.Country

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val iso: String,
    val image: String?
)

fun CountryEntity.asCountry() = Country(
    id = id,
    name = name,
    iso = iso,
    image = image
)


fun Country.asEntity() = CountryEntity(
    id = id,
    name = name,
    iso = iso,
    image = image
)