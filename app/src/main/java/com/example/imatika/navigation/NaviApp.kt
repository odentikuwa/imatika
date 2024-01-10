package com.example.imatika.navigation


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    fun NavGraphApp(navController: NavHostController) {
        NavHost(navController, startDestination = "firstScreen") {
            val text: String = "イマ\nチカ"
            composable("firstScreen") { Greeting(text, navController) }
            composable("secondScreen") { SecondScreen() }
        }
    }

    @Composable
    fun Greeting(text: String, navController: NavHostController, modifier: Modifier = Modifier) {
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

    @Composable
    fun SecondScreen() {
        var location by remember { mutableStateOf("") }
        val context = LocalContext.current
        var permissionGranted by remember { mutableStateOf(false) }
        var restaurants by remember { mutableStateOf(emptyList<Restaurant>()) }
        var latitude by remember { mutableStateOf(0.0) }
        var longitude by remember { mutableStateOf(0.0) }

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
                        latitude = latitudeString?.toDouble() ?: 0.0

                        // 経度の取得
                        val longitudeRegex = Regex("経度: ([^,]+)")
                        val longitudeMatch = longitudeRegex.find(result)
                        val longitudeString = longitudeMatch?.groups?.get(1)?.value
                        longitude = longitudeString?.toDouble() ?: 0.0

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

        //パーミッションの許可・未許可でUI表示内容を切り替える
        if (permissionGranted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // GoogleMapComponentのUIを表示
                GetGoogleMapComponent(context = context, latitude = latitude, longitude = longitude)
                // RestaurantListのUIを表示
                RestaurantList(restaurants)
            }
        } else {
            // UI を表示
            Column {
                Text(text = location)
            }
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

    // RestaurantList コンポーネント
    @Composable
    fun RestaurantList(restaurants: List<Restaurant>) {
        //リストを水平にスクロール
        LazyRow {
            items(restaurants) { restaurant ->
                RestaurantItem(restaurant)
            }
        }
    }

    // RestaurantItem コンポーネント
    @Composable
    fun RestaurantItem(restaurant: Restaurant) {
        // 各レストランの情報を表示する UI コンポーネントを実装
        Box(
            modifier = Modifier
                .width(400.dp) // 各アイテムの横幅を設定
                .height(150.dp) // 各アイテムの縦幅を設定
                .padding(20.dp)
                .clip(RoundedCornerShape(18.dp)) // 角を丸くする
                .background(Color.Cyan.copy(alpha = 0.7f)) // 背景色を水色に設定し、透明度を指定
        ) {
            val padding = 16.dp
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(Modifier.size(padding)) // paddingを16dpに設定
//                    Spacer(modifier = Modifier.width(16.dp)) // 文字とアイテムの水平方向の間隔を16dpに設定
                    Text(text = restaurant.name, fontWeight = FontWeight.Bold, fontSize = 18.sp,modifier = Modifier.padding(start = 16.dp))
                    Text(text = restaurant.vicinity, fontSize = 14.sp,modifier = Modifier.padding(start = 16.dp))
                }
            }
            // 白い右向きの三角形アイコン
            Row(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }
    }
}

