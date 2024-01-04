package com.example.imatika.navigation


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.imatika.model.Restaurant
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Navigation {
    @Composable
    fun navGraphApp(navController: NavHostController) {
        NavHost(navController, startDestination = "firstScreen") {
            val text: String = "イマ\nチカ"
            composable("firstScreen") { greeting(text, navController) }
            composable("secondScreen") { secondScreen() }
        }
    }

    @Composable
    fun greeting(text: String, navController: NavHostController, modifier: Modifier = Modifier) {
        Text(
            text = text,
            // クリック時
            modifier = Modifier.clickable {
                // 画面遷移を実行
                navController.navigate("secondScreen")
            }
                .fillMaxSize()
                .wrapContentSize(Alignment.Center), // テキストを上下中央に配置
            fontFamily = FontFamily.SansSerif,
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 60.sp,
                color = Color.Black, // テキストの色を設定
                fontWeight = FontWeight.Bold, // テキストの太さを設定
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun secondScreen() {
        var location by remember { mutableStateOf("") }
        val context = LocalContext.current
        var permissionGranted by remember { mutableStateOf(false) }
        var restaurants by remember { mutableStateOf(emptyList<Restaurant>()) }

        // パーミッションのリクエスト
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            // パーミッションの許可状態が変更されたら変数を更新
            permissionGranted = isGranted
            Log.d("SecondScreen", "パーミッションの許可状態が変更されて変数を更新")
        }

        // パーミッションのリクエスト
        LaunchedEffect(true) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            Log.d("SecondScreen", "requestPermissionLauncher.launch実行済み")
        }

        // パーミッションの許可状態が変更されたら非同期処理を開始
        LaunchedEffect(permissionGranted) {
            if (permissionGranted) {
                withContext(Dispatchers.IO) {
                    // 位置情報の取得を行うsuspend関数
                    val result = fetchLocation(context)

                    // ComposeスレッドでUIを更新
                    withContext(Dispatchers.Main) {
                        location = result
                        Log.d("SecondScreen", "result:${location}")
                        Log.d("SecondScreen", "UI更新true")

                        // 緯度の取得
                        val latitudeRegex = Regex("緯度: ([^,]+)")
                        val latitudeMatch = latitudeRegex.find(result)
                        val latitudeString = latitudeMatch?.groups?.get(1)?.value
                        val latitude: Double = latitudeString?.toDouble() ?: 0.0

                        // 経度の取得
                        val longitudeRegex = Regex("経度: ([^,]+)")
                        val longitudeMatch = longitudeRegex.find(result)
                        val longitudeString = longitudeMatch?.groups?.get(1)?.value
                        val longitude: Double = longitudeString?.toDouble() ?: 0.0

                        // 周辺のグルメ情報を取得
                        restaurants = getNearbyRestaurants(context = context, latitude = latitude,longitude = longitude)
                    }
                }
            } else {
                // パーミッションが拒否された場合の処理
                // 必要に応じてユーザーにメッセージを表示するなどの対応を行う
                location = "位置情報の取得には許可が必要です"
                Log.d("SecondScreen", "UI更新false")
            }
        }
        // UI を表示
        Column {
            Text(text = location)
            // 周辺のグルメ情報を一覧表示
//            RestaurantList(restaurants)
        }
    }

    // 非同期処理を行うsuspend関数
    suspend fun fetchLocation(context: Context): String {
        // ここに非同期の処理を記述
        return withContext(Dispatchers.IO) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try {
                    // 許可されている場合は位置情報を取得
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    Log.d("SecondScreen", "位置情報取得")
                    // 位置情報を非同期に取得
                    val lastLocation = fusedLocationClient.lastLocation.await()
                    Log.d("SecondScreen", "位置情報を非同期に取得")
                    Log.d("SecondScreen", "緯度: ${lastLocation.latitude}, 経度: ${lastLocation.longitude}")
                    "緯度: ${lastLocation.latitude}, 経度: ${lastLocation.longitude}"
                } catch (exception: Exception) {
                    "位置情報の取得中にエラーが発生しました"
                }
            } else {
                // 許可されていない場合は許可を求める
                ""
            }
        }
    }

//    // RestaurantList コンポーネント
//    @Composable
//    fun RestaurantList(restaurants: List<Restaurant>) {
//        LazyColumn {
//            items(restaurants) { restaurant ->
//                RestaurantItem(restaurant = restaurant)
//            }
//        }
//    }
//
//    // RestaurantItem コンポーネント
//    @Composable
//    fun RestaurantItem(restaurant: Restaurant) {
//        // 各レストランの情報を表示する UI コンポーネントを実装
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text(text = restaurant.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
//            Text(text = restaurant.vicinity, fontSize = 14.sp)
//        }
//    }
}

