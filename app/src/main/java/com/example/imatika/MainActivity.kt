package com.example.imatika


import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.imatika.navigation.Navigation
import com.example.imatika.ui.theme.ImatikaTheme

//@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
//@AndroidEntryPoint
class MainActivity() : ComponentActivity(), Parcelable {
    constructor(parcel: Parcel) : this() {
        // シリアライズ時の処理（読み込み時のデータの復元など）を実装する
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        // シリアライズ時の処理（データの書き込みなど）を実装する
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImatikaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) { //背景色
                }
            }
            //コントローラ
            val navController = rememberNavController()
            val navigation = Navigation()
            navigation.navGraphApp(navController)
        }
    }

}
