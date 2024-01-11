package com.example.imatika.navigation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.launch

// GoogleMap の青い点を表示するための Marker 変数を定義
private var locationMarker: Marker? = null

/**
 * Google マップコンポーネントを表示する Composable 関数。
 *
 * @param context 関数に渡すコンテキスト パラメータ。
 * @param latitude マップ上に表示する場所の緯度。
 * @param longitude マップ上に表示する場所の経度。
 */
@Composable
fun GetGoogleMapComponent(
    context: Context, // contextをパラメータとして渡す
    latitude: Double,
    longitude: Double
) {
    // LocalContextを使わずにライフサイクルを取得
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = rememberMapViewWithLifecycle(lifecycleOwner)

    // AndroidView を使って MapView を表示
    AndroidView(factory = { mapView }) { googleMap ->
        // マップが準備できたときのコールバック
        googleMap.getMapAsync { map ->
            // カメラを指定された場所に移動
            val latLng = LatLng(latitude, longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            // 現在地の青い点を表示
            locationMarker?.remove()
            locationMarker = map.addMarker(
                MarkerOptions().position(latLng).title("現在地").icon(
                    BitmapDescriptorFactory.fromBitmap(getBlueCircleBitmap(context))
                )
            )
            // その他の地図の設定はここに追加できます
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(lifecycleOwner: LifecycleOwner): MapView {
    val context = LocalContext.current

    // ライフサイクルと一緒に MapView を記録
    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
            onResume()
        }
    }
    // DisposableEffect を使って MapView のライフサイクルを監視
    DisposableEffect(Unit) {
        val observer = MapViewLifecycleObserver(mapView)

        // LocalContextを使わずにライフサイクルを取得
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    return mapView
}

/**
 * MapView のライフサイクルを監視するためのライフサイクル オブザーバー。
 *
 * @param mapView 監視対象の MapView。
 */
class MapViewLifecycleObserver(private val mapView: MapView) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        // MapView のライフサイクル イベントを処理
        when (event) {
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> Unit
        }
    }
}

//円の描画内容
//BitmapではなくImageBitmapを使用するのが適切
//Androidの通常のUI描画
fun getBlueCircleBitmap(context: Context): Bitmap {
    val diameter = 50 // 円の直径
    //Contextパラメータを受け入れ、それを使用してディスプレイメトリクスの密度を取得
    val density = context.resources.displayMetrics.density
//    val bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
    //Contextパラメータ使用により、画面の密度に合わせて円のサイズを調整可能
    val bitmap = Bitmap.createBitmap((diameter * density).toInt(), (diameter * density).toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 円を描画
    val paint = Paint().apply {
        color = Color.CYAN
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint)

    // 境界線（ストローク）を追加して円を強調
    val strokePaint = Paint().apply {
        color = Color.BLACK // 境界線の色
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 4f // 境界線の太さ
    }
    canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f - 2f, strokePaint) // 半径からストロークの幅を差し引くことで円が画面内に収まるように調整
    return bitmap
}


