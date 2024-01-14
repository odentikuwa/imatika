package com.example.imatika.model

import kotlinx.serialization.Serializable

data class Restaurant(
    var name: String,
    val vicinity: String,
    var latitude: Double,
    var longitude: Double
)

// Google Places APIのレスポンスを表すデータクラス
@Serializable
data class GooglePlacesResponse(
    val results: List<Place>
)

// Place データクラス
@Serializable
data class Place(
    val name: String,
    val vicinity: String,
    val geometry: Geometry
)

// Geometry データクラス
@Serializable
data class Geometry(
    val location: Location
)

// Location データクラス
@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)