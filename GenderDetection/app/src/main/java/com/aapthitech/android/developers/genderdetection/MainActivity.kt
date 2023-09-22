package com.aapthitech.android.developers.genderdetection

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.ExifInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    // Initialize the MLKit FaceDetector
    private val realTimeOpts =
        FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
    private val firebaseFaceDetector = FaceDetection.getClient(realTimeOpts)

    lateinit var genderModelInterpreter: Interpreter
    private lateinit var genderClassificationModel: GenderClassificationModel
    private val REQUEST_IMAGE_SELECT = 102

    // Boolean values to check for NNAPI and Gpu Delegates
    private var useNNApi: Boolean = false
    private var useGpu: Boolean = false
    private val modelNames = arrayOf(
        "Age/Gender Detection Model ( Quantized ) ",
        "Age/Gender Detection Model ( Non-quantized )",
        "Age/Gender Detection Lite Model ( Quantized )",
        "Age/Gender Detection Lite Model ( Non-quantized )",
    )

    /*// Filepaths of the models ( in the assets folder ) corresponding to the models in `modelNames`.
    private val modelFilenames = arrayOf(
        arrayOf("model_age_q.tflite", "model_gender_q.tflite"),
        arrayOf("model_age_nonq.tflite", "model_gender_nonq.tflite"),
        arrayOf("model_lite_age_q.tflite", "model_lite_gender_q.tflite"),
        arrayOf("model_lite_age_nonq.tflite", "model_lite_gender_nonq.tflite"),
    )*/
    private var modelFilename = arrayOf("model_lite_gender_q.tflite")
    private lateinit var finalBitmap: Bitmap
    private lateinit var detectGender: Button
    private lateinit var uploadImage: Button
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sampleImageView: ImageView
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var inferenceSpeedTextView: TextView
    private val shift = 5


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        detectGender = findViewById(R.id.find_gender)
        uploadImage = findViewById(R.id.Upload_image)
        sampleImageView = findViewById(R.id.gender_find_image)
        inferenceSpeedTextView = findViewById(R.id.identified_gender)
        // A ProgressDialog to notify the user that the images are being processed.
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Identifing for faces ...")

        val options = Interpreter.Options().apply {
            addDelegate(NnApiDelegate())
        }
        // Initialize the models in a coroutine.
        coroutineScope.launch {
            initModels(options)
        }
        val drawableResource = R.drawable.virat

        // Convert the drawable resource to a Bitmap
        val bitmap: Bitmap = BitmapFactory.decodeResource(resources, drawableResource)
        finalBitmap= bitmap
        detectGender.setOnClickListener {
       
//            detectFaces(finalBitmap)
        }
        uploadImage.setOnClickListener {
            dispatchSelectPictureIntent()

        }
    }

    // Suspending function to initialize the TFLite interpreters.
    private suspend fun initModels(options: Interpreter.Options) =
        withContext(Dispatchers.Default) {
            genderModelInterpreter =
                Interpreter(FileUtil.loadMappedFile(applicationContext, modelFilename[0]), options)
            withContext(Dispatchers.Main) {

                genderClassificationModel = GenderClassificationModel().apply {
                    interpreter = genderModelInterpreter
                }
                // Notify the user once the models have been initialized.
                Toast.makeText(applicationContext, "Models initialized.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
//        ageModelInterpreter.close()
        genderModelInterpreter.close()
    }

    // Dispatch an Intent which opens the gallery application for the user.
    private fun dispatchSelectPictureIntent() {
        val selectPictureIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(selectPictureIntent, REQUEST_IMAGE_SELECT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If the user opened the camera

        // if the user selected an image from the gallery
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_SELECT) {
            val inputStream = contentResolver.openInputStream(data?.data!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
//            progressDialog.show()

            // Pass the clicked picture to `detectFaces`.
//            detectFaces( bitmap!!)
            sampleImageView.setImageBitmap(bitmap)

            finalBitmap = bitmap!!
        }
    }

    private fun detectFaces(image: Bitmap) {
        val inputImage = InputImage.fromBitmap(image, 0)
        // Pass the clicked picture to MLKit's FaceDetector.
        firebaseFaceDetector.process(inputImage).addOnSuccessListener { faces ->
            if (faces.size != 0) {
                // Set the cropped Bitmap into sampleImageView.
                sampleImageView.setImageBitmap(cropToBBox(image, faces[0].boundingBox))
                // Launch a coroutine
                coroutineScope.launch {

                    // Predict the age and the gender.
//                        val age = ageEstimationModel.predictAge(cropToBBox(image, faces[0].boundingBox))
                    val gender = genderClassificationModel.predictGender(
                        cropToBBox(
                            image, faces[0].boundingBox
                        )
                    )

                    // Show the inference time to the user via `inferenceSpeedTextView`.
//

                    // Show the final output to the user.
                    inferenceSpeedTextView.text = if (gender[0] > gender[1]) {
                        "Male"
                    } else {
                        "Female"
                    }
                    progressDialog.dismiss()
                }
            } else {
                // Show a dialog to the user when no faces were detected.
                progressDialog.dismiss()
                val dialog = AlertDialog.Builder(this).apply {
                    title = "No Faces Found"
                    setMessage(
                        "We could not find any faces in the image you just clicked. " + "Try clicking another image or improve the lightning or the device rotation."
                    )
                    setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }
                    setCancelable(false)
                    create()
                }
                dialog.show()
            }


        }
    }


    private fun cropToBBox(image: Bitmap, bbox: Rect): Bitmap {
        return Bitmap.createBitmap(
            image,
            bbox.left - 0 * shift,
            bbox.top + shift,
            bbox.width() + 0 * shift,
            bbox.height() + 0 * shift
        )
    }

}