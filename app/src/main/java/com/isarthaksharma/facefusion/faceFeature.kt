package com.isarthaksharma.facefusion

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.isarthaksharma.facefusion.databinding.ActivityFaceFeatureBinding
import com.isarthaksharma.facefusion.databinding.CameraLayoutBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class faceFeature : AppCompatActivity() {
    //permission CODE:
    private val storagePermissionCode:Int = 1001
    private val cameraPermissionCode:Int = 1002

    //viewBinding
    private lateinit var faceBinding: ActivityFaceFeatureBinding
    private lateinit var cameraLayoutBinding: CameraLayoutBinding
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        faceBinding = ActivityFaceFeatureBinding.inflate(layoutInflater)
        setContentView(faceBinding.root)

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        showChoiceDialogBox()
        // source choice
        faceBinding.imageSelect.setOnClickListener{
            showChoiceDialogBox()
        }
        // re-selecting source choice
        faceBinding.reloadBtn.setOnClickListener {
            showChoiceDialogBox()
        }

    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++..+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // hosts choices dialog box
    private fun showChoiceDialogBox() {
        val choice = arrayOf("Camera","Gallery")
        val builder =AlertDialog.Builder(this)
        builder.setTitle("Select the Source : ")
        builder.setIcon(R.drawable.app_icon)
        builder.setItems(choice){ _, which ->
            when (which) {
                0 -> launchCamera()
                1 -> launchGallery()
            }
        }
        builder.show()
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++..+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private fun launchCamera() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),cameraPermissionCode)
        }else{
            launch_CameraX()
            Toast.makeText(this, "Working on CameraX", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launch_CameraX() {
        cameraLayoutBinding = CameraLayoutBinding.inflate(layoutInflater)
        setContentView(cameraLayoutBinding.root)
        var flagRotate: Boolean

        // Using CameraController
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // CameraProvider
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(cameraLayoutBinding.viewFinder.surfaceProvider)
            }

            // ImageCapture
            imageCapture = ImageCapture.Builder().build()

            // Camera Selector
            var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            flagRotate = true

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

            // Rotation screen
            cameraLayoutBinding.cameraRotate.setOnClickListener {
                cameraSelector = if (flagRotate) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                flagRotate = !flagRotate

                try {
                    // Unbind all use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        this as LifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }
            }

            // Capture button
            cameraLayoutBinding.captureButton.setOnClickListener {
                captureImage()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage() {
        setContentView(faceBinding.root)

        val outputDirectory = getOutputDirectory()
        val photoFile = createFile(outputDirectory, "yyyyMMdd_HHmmss", ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    processImageUri(savedUri)
                    faceBinding.CaptureHeading.visibility = View.VISIBLE
                    faceBinding.imgCaptured.visibility = View.VISIBLE
                    faceBinding.imageSelect.visibility = View.GONE
                    faceBinding.imageAfterProcessing.setImageURI(savedUri)
                    faceBinding.imageAfterProcessing.visibility = View.VISIBLE
                    faceBinding.ResultBox.visibility = View.VISIBLE
                    Toast.makeText(this@faceFeature, "Image Captured", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Image capture failed: ${exception.message}", exception)
                    Toast.makeText(this@faceFeature, "Image capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }


    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "scannify").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun createFile(baseFolder: File, format: String, extension: String): File {
        val timestamp = SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis())
        return File(baseFolder, "$timestamp$extension")
    }


    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++..+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //Gallery Permission and Selection
    //if greater than android 13 use ReadMediaImages else use External storage
    private fun launchGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), storagePermissionCode)
        } else {
            openGallery()
        }
    }
    private fun openGallery() {
        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // image Picker
    private val imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri->
        if(uri != null){
            //after picking image setting on the screen
            faceBinding.imageSelect.visibility = View.GONE
            processImageUri(uri)
            Toast.makeText(this, "Processing Image.......", Toast.LENGTH_SHORT).show()
            faceBinding.CaptureHeading.visibility = View.VISIBLE
            faceBinding.imageAfterProcessing.setImageURI(uri)
            faceBinding.imageAfterProcessing.visibility = View.VISIBLE
            faceBinding.ResultBox.visibility = View.VISIBLE
        }else{
            Toast.makeText(this,"No Image was selected",Toast.LENGTH_SHORT).show()
        }
    }


    // Camera & Storage
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            storagePermissionCode -> {
                if (requestCode == storagePermissionCode) {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        openGallery()
                    } else {
                        // Handle the case where the user denied the permission
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            cameraPermissionCode->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    launchCamera()
                }else{
                    Toast.makeText(this, "Camera permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++..+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // face Detection algo
    private fun processImageUri(uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            detectFace(bitmap)
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun detectFace(bitmap: Bitmap) {
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(highAccuracyOpts)
        val image = InputImage.fromBitmap(bitmap, 0)

        detector.process(image)
            .addOnSuccessListener { faces ->
                var resultText = ""
                var collectedStr: String
                var i = 1
                for (face in faces) {
                    collectedStr = "\nFace Number :$i " +
                            "\nSmiling : ${face.smilingProbability?.times(100)}%" +
                            "\nLeft Eye Opened: ${face.leftEyeOpenProbability?.times(100)}%" +
                            "\nRight Eye Opened: ${face.rightEyeOpenProbability?.times(100)}%" +
                            "\n\n"
                    i++
                    resultText += collectedStr
                }

                if (faces.isEmpty()) {
                    faceBinding.textResultMsg.text = "NO FACE was detected\nPlease Retry"
                } else {
                    faceBinding.textResultMsg.text = resultText
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Something went wrong\n ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}
