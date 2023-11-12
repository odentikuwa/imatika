package com.example.imatika.navigation


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import com.google.android.gms.location.LocationServices

class Navigation {
    @Composable
    fun NavGraphApp(navController: NavHostController) {
        NavHost(navController, startDestination = "firstScreen") {
            val text: String = "イマ\nチカ"
            composable("firstScreen") { Greeting(text,navController) }
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


    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun SecondScreen() {
//    Text("This is the Second Screen")

        var location by remember { mutableStateOf("") }
        val context = LocalContext.current
        val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

        // 位置情報の取得
        LaunchedEffect(Unit) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val lastLocation = fusedLocationClient.lastLocation.result
                location = "緯度: ${lastLocation.latitude}, 経度: ${lastLocation.longitude}"
            } else {
                location = "位置情報のパーミッションがありません"
            }
        }

        Text(text = location)
    }
}