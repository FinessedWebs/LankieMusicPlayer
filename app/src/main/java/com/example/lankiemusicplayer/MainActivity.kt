package com.example.lankiemusicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lankiemusicplayer.navigation.AppNavigation
import com.example.lankiemusicplayer.ui.theme.LankieMusicPlayerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("LankieMusic", "App started successfully")

        requestAudioPermission()

        setContent {
            LankieMusicPlayerTheme {
                Surface {
                    AppNavigation()
                }
            }
        }
    }

    private fun requestAudioPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                100
            )
        }
    }



}