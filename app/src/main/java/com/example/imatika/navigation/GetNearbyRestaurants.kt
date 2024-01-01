package com.example.imatika.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.imatika.model.Restaurant
import kotlinx.coroutines.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import android.content.Context

// 近くのレストランを取得する関数
suspend fun getNearbyRestaurants(
    latitude: Double,
    longitude: Double,
    radius: Int = 1000,
): List<Restaurant> = runBlocking {
    // Google Places APIキー
    val apiKey = context.getString(R.string.google_maps_key) // ご自身のGoogle Places APIキーに置き換えてください
    val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=$latitude,$longitude" +
            "&radius=$radius" +
            "&type=restaurant" +
            "&key=$apiKey"

    // Ktor HttpClientを作成
    val client = HttpClient()

    try {
        // HTTPリクエストを送信して結果を取得
        val response: String = client.get(url)

        // 結果からレストラン情報を抽出
        val results = Json { ignoreUnknownKeys = true }.decodeFromString<GooglePlacesResponse>(response).results
        val restaurants = mutableListOf<Restaurant>()

        for (place in results) {
            val name = place.name
            val vicinity = place.vicinity

            restaurants.add(Restaurant(name, vicinity))
        }

        return@runBlocking restaurants
    } finally {
        // HttpClientをクローズ
        client.close()
    }
}

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

data class Restaurant(
    val name: String,
    val vicinity: String
)