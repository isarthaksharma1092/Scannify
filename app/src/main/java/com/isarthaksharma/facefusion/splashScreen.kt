package com.isarthaksharma.facefusion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class splashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            val splashScreen = findViewById<VideoView>(R.id.splashScreen)
            val videoPath = "android.resource://" + packageName + "/" + R.raw.splash_screen
            splashScreen.setVideoURI(Uri.parse(videoPath))
            window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
            splashScreen.setOnCompletionListener {
                val mainIntent = Intent(this@splashScreen, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
            splashScreen.start()

            insets
        }

    }
}