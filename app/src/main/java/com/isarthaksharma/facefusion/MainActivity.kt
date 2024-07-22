package com.isarthaksharma.facefusion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.isarthaksharma.facefusion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var homeBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        homeBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            homeBinding.faceBtn.setOnClickListener {
                Toast.makeText(this,"Let's Capture",Toast.LENGTH_LONG).show()
                startActivity(Intent(this,faceFeature::class.java))
            }

            homeBinding.textBtn.setOnClickListener {
                Toast.makeText(this,"Text Capture",Toast.LENGTH_LONG).show()
            }
            insets
        }
    }
}