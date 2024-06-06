package com.isarthaksharma.facefusion

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.isarthaksharma.facefusion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var myBinging : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        myBinging = ActivityMainBinding.inflate(layoutInflater)
        setContentView(myBinging.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            //code s
            myBinging.cameraBtn.setOnClickListener{
                myBinging.back.visibility= View.INVISIBLE
                myBinging.imgCaptured.visibility= View.GONE
                myBinging.imageAfterProcessing.visibility = View.GONE
                myBinging.cameraBtn.visibility = View.VISIBLE
                myBinging.ResultBox.visibility = View.GONE

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if(intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent,123)
                }else {
                    Toast.makeText(this,"Oops something went Wrong 🤔",Toast.LENGTH_SHORT).show()
                }
            }
            //code e
            insets
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123  && resultCode == RESULT_OK){

            val extras = data?.extras
            //here we will be collecting the data in Bitmap format (it was required by the ML libreries)
            val bitmap = extras?.get("data") as? Bitmap
            detectFace(bitmap)
        }else{
            Toast.makeText(this,"Captured Not matches",Toast.LENGTH_SHORT).show()
        }
        myBinging.back.visibility= View.VISIBLE
        myBinging.back.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    private fun detectFace(bitmap: Bitmap?) {
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        var detector = FaceDetection.getClient(highAccuracyOpts)
        val image = bitmap?.let { InputImage.fromBitmap(it,0) }
        //setting up the image
        myBinging.imgCaptured.visibility= View.VISIBLE
        myBinging.imageAfterProcessing.visibility = View.VISIBLE
        myBinging.imageAfterProcessing.setImageBitmap(bitmap)

        myBinging.cameraBtn.visibility = View.GONE
        myBinging.ResultBox.visibility = View.VISIBLE

        val result = image?.let {
            detector.process(it)

                .addOnSuccessListener { faces ->
                    // Task completed successfully
                    // ...
                    var resutlText = " "
                    var i =1
                    for(face in faces){
                        resutlText = "Face Number : $i" +
                                "\n\nSmile : ${face.smilingProbability?.times((100))} %"+
                                "\n\nLeft Eye Open : ${face.leftEyeOpenProbability?.times(100)} %"+
                                "\n\nRight Eye Open : ${face.rightEyeOpenProbability?.times(100)} %"
                        i++
                    }
                    if(faces.isEmpty()){
                        myBinging.textResultMsg.text = "No Face Detected"
                    }else{
                        myBinging.textResultMsg.text = resutlText
                    }

                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                    Toast.makeText(this, "Exception Happened log error\n$e",Toast.LENGTH_LONG).show()
                }
        }

    }
}