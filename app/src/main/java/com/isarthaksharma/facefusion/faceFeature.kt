package com.isarthaksharma.facefusion

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.isarthaksharma.facefusion.databinding.ActivityFaceFeatureBinding

class faceFeature : AppCompatActivity() {
    private lateinit var faceBinding:ActivityFaceFeatureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        faceBinding = ActivityFaceFeatureBinding.inflate(layoutInflater)
        setContentView(faceBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        accessCamera()
        faceBinding.reload.setOnClickListener {
            accessCamera()
        }
    }

    private fun accessCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager)!= null){
            startActivityForResult(intent,1900)

        }else{
            Toast.makeText(this,"Opps!\nSomething went wrong",Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1900 && resultCode == RESULT_OK && data != null){
            val extras = data.extras
            val bitmap = extras?.get("data") as? Bitmap
            faceBinding.imageAfterProcessing.setImageBitmap(bitmap)
            if (bitmap != null) {
                faceBinding.imageAfterProcessing.setImageBitmap(bitmap)
                detectFace(bitmap)
            }
        }
    }

    private fun detectFace(bitmap: Bitmap?) {
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(highAccuracyOpts)
        val image = bitmap?.let { InputImage.fromBitmap(it, 0) }

        val result = image?.let {
            detector.process(it)
                .addOnSuccessListener { faces ->
                    // Task completed successfully
                    var resultText = " "
                    var collectedStr:String = " "
                    var i = 1
                    for(face in faces){
                        collectedStr = "Face Number :$i " +
                                "\nSmiling : ${face.smilingProbability?.times(100)}%" +
                                "\nLeft Eye Opened: ${face.leftEyeOpenProbability?.times(100)}%" +
                                "\nRight Eye Opened: ${face.rightEyeOpenProbability?.times(100)}%" +
                                "\n\n "
                        i++
                        resultText += collectedStr
                    }
                    if(faces.isEmpty()){
                        faceBinding.textResultMsg.text = "NO FACE was detected\nPlease Retry"
                    }else{
                        faceBinding.textResultMsg.text = resultText
                        Toast.makeText(this,resultText,Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Toast.makeText(this,"Something went wrong\n ${e.message}",Toast.LENGTH_LONG).show()
                }
        }
    }
}







