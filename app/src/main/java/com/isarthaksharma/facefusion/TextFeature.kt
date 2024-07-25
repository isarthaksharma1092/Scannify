package com.isarthaksharma.facefusion

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.isarthaksharma.facefusion.databinding.ActivityTextFeatureBinding
import java.io.IOException

class TextFeature : AppCompatActivity() {

    private lateinit var textBinding: ActivityTextFeatureBinding

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            processImageUri(uri)
        } else {
            Toast.makeText(this, "Image selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textBinding = ActivityTextFeatureBinding.inflate(layoutInflater)
        setContentView(textBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val copyContentBtn = findViewById<ImageView>(R.id.CopyContentBtn)
        val cameraBtn = findViewById<ImageView>(R.id.cameraBtn)
        val eraseBtn = findViewById<ImageView>(R.id.eraseBtn)

        // copy everything
        copyContentBtn.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", textBinding.editTextView.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        // remove everything
        eraseBtn.setOnClickListener {
            textBinding.clickOpenGallery.visibility = View.VISIBLE
            textBinding.editTextView.visibility = View.GONE
            Toast.makeText(this, "Everything erased", Toast.LENGTH_SHORT).show()
        }

        //clicking pictures from Camera
        cameraBtn.setOnClickListener {
            cameraClicked()
        }

        //picking image from gallery
        textBinding.clickOpenGallery.setOnClickListener {
            pickImageLauncher.launch("image/*")
            Toast.makeText(this, "Opening Gallery ....", Toast.LENGTH_SHORT).show()
        }



    }

    private fun processImageUri(uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            detectText(bitmap)
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cameraClicked() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 9002)
        } else {
            Toast.makeText(this, "Oops something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9002 && resultCode == RESULT_OK && data != null) {
            val extras = data.extras
            val bitmap = extras?.get("data") as Bitmap
            detectText(bitmap)
        }
    }

    private fun detectText(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        textBinding.editTextView.visibility = View.VISIBLE
        textBinding.clickOpenGallery.visibility = View.GONE
        val recognizerLatin = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizerLatin.process(image)
            .addOnSuccessListener { visionText ->

                textBinding.editTextView.setText(visionText.text)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
