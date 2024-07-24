package com.isarthaksharma.facefusion

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.isarthaksharma.facefusion.databinding.ActivityTextFeatureBinding

class TextFeature : AppCompatActivity() {

    private lateinit var textBinding: ActivityTextFeatureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        textBinding = ActivityTextFeatureBinding.inflate(layoutInflater)
        setContentView(textBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val CopyContentBtn = findViewById<ImageView>(R.id.CopyContentBtn)
        val cameraBtn = findViewById<ImageView>(R.id.cameraBtn)
        val eraseBtn = findViewById<ImageView>(R.id.eraseBtn)

        CopyContentBtn.setOnClickListener {
            copyContent()
        }
        cameraBtn.setOnClickListener {
            cameraClicked()
        }
        eraseBtn.setOnClickListener {
            textBinding.editTextView.hint = "Scan something to see !"
            Toast.makeText(this,"Everything erased",Toast.LENGTH_LONG).show()
        }
    }

    private fun copyContent(){
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label",textBinding.editTextView.text.toString())
        clipBoard.setPrimaryClip(clip)
        Toast.makeText(this,"Copied to clipboard",Toast.LENGTH_LONG).show()

    }
    private fun cameraClicked() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if(intent.resolveActivity(packageManager) != null){
            // take the image and send it for text extraction
            startActivityForResult(intent,9002)
            val bitmap = intent.data

        }else{
            Toast.makeText(this,"Oops something went wrong !",Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 9002 && resultCode == RESULT_OK && data != null){
            val extras = data.extras
            val bitmap = extras?.get("data") as Bitmap
            detectText(bitmap)
        }
    }

    private fun detectText(bitmap: Bitmap) {
        var flag:Boolean = true
        val image = InputImage.fromBitmap(bitmap, 0)

        // When using Latin script library
        val recognizerLatin = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val resultLatin = recognizerLatin.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                // ...
                textBinding.editTextView.setText(visionText.text.toString())
                flag = true
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                flag = false
                Toast.makeText(this,"Error: ${e.message}",Toast.LENGTH_LONG).show()
            }

    }
}