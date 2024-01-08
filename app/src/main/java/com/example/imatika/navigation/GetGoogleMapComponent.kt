package com.example.imatika.navigation

import android.content.Context
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
import com.example.imatika.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng

@Composable
fun GetGoogleMapComponent(
    context: Context, // contextをパラメータとして渡す
    latitude: Double,
    longitude: Double
) {
    val apiKey = context.getString(R.string.google_maps_key)

    // LocalContextを使わずにライフサイクルを取得
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = rememberMapViewWithLifecycle(lifecycleOwner)

    AndroidView(factory = { mapView }) { googleMap ->
        googleMap.getMapAsync { map ->
            val latLng = LatLng(latitude, longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            // その他の地図の設定はここに追加できます
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(lifecycleOwner: LifecycleOwner): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
            onResume()
        }
    }

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

class MapViewLifecycleObserver(private val mapView: MapView) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> Unit
        }
    }
}


