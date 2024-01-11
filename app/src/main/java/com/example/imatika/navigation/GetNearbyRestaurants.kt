package com.example.imatika.navigation

import android.content.Context
import com.example.imatika.R
import com.example.imatika.model.GooglePlacesResponse
import com.example.imatika.model.Restaurant
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

// 共通の Json インスタンスを作成
private val json = Json { ignoreUnknownKeys = true }

// 近くのレストランを取得する関数
suspend fun getNearbyRestaurants(
    context: Context, // contextをパラメータとして渡す
    latitude: Double, // 緯度
    longitude: Double, // 経度
    radius: Int = 1000, //円の半径をメートル単位で指定
): List<Restaurant> = runBlocking {
    // Google Places APIキー
    val apiKey = context.getString(R.string.google_maps_key) // ご自身のGoogle Places APIキーに置き換えてください
    val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=$latitude,$longitude" +
            "&radius=$radius" +
            "&type=restaurant" +
            "&key=$apiKey" +
            "&language=ja" // APIのレスポンスが日本語で返されるかどうかは、Google Placesに登録されたデータ次第

    return@runBlocking HttpClient().use { client ->
        try {
            // HTTPリクエストを送信してHttpResponseを取得
            val response: HttpResponse = client.get(url)

            // HttpResponseから文字列を取得
            val responseString: String = response.bodyAsText()

            // 結果からレストラン情報を抽出
            val results = json.decodeFromString<GooglePlacesResponse>(responseString).results
            val restaurants = mutableListOf<Restaurant>()

            for (place in results) {
                val name = place.name
                val vicinity = place.vicinity
                val localLatitude = place.geometry.location.lat
                val localLongitude = place.geometry.location.lng

                restaurants.add(Restaurant(name, vicinity, localLatitude, localLongitude))
            }

            return@use restaurants
        } finally {
            // HttpClientをクローズ
            // このブロック内で非同期処理を行っていないため、use ブロック内で実行可能
        }
    }
}


