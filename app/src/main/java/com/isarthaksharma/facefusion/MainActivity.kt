package com.isarthaksharma.facefusion

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.isarthaksharma.facefusion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var homeBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(1500)
        installSplashScreen()
        enableEdgeToEdge()

        homeBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        homeBinding.faceBtn.setOnClickListener {
            startActivity(Intent(this, faceFeature::class.java))
        }

        homeBinding.textBtn.setOnClickListener {
            startActivity(Intent(this, TextFeature::class.java))
        }
    }
}
