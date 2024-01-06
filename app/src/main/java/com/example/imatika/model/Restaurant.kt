package com.example.imatika.model

import kotlinx.serialization.Serializable

data class Restaurant(
    val name: String,
    val vicinity: String
)

// Google Places APIのレスポンスを表すデータクラス
@Serializable
data class GooglePlacesResponse(
    val results: List<Place>
)

@Serializable
data class Place(
    val name: String,
    val vicinity: String
)