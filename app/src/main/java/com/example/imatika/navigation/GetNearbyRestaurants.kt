package com.example.imatika.navigation

import android.content.Context
import com.example.imatika.R
import com.example.imatika.model.Restaurant
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


// 近くのレストランを取得する関数
suspend fun getNearbyRestaurants(
    context: Context, // contextをパラメータとして渡す
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
        // HTTPリクエストを送信してHttpResponseを取得
        val response: HttpResponse = client.get(url)

        // HttpResponseから文字列を取得
        val responseString: String = response.bodyAsText()

        // 結果からレストラン情報を抽出
        val results = Json { ignoreUnknownKeys = true }.decodeFromString<GooglePlacesResponse>(responseString).results
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