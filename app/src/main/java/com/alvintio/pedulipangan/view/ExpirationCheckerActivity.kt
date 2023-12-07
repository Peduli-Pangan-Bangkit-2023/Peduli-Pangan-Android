package com.alvintio.pedulipangan.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.SyncStateContract.Helpers
import android.view.Surface
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.util.Consumer
import androidx.exifinterface.media.ExifInterface
import com.alvintio.pedulipangan.databinding.ActivityExpirationCheckerBinding
import com.alvintio.pedulipangan.util.ViewUtils
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ExpirationCheckerActivity : AppCompatActivity() {

    companion object {
        private val REQUIRED_CAMERA_PERMISS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISS = 101
    }

    private lateinit var binding: ActivityExpirationCheckerBinding

    private var pathImg: String = ""

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = it.data?.data
            selectedImageUri?.let {
                binding.ivProductImage.setImageURI(it)
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val file = File(pathImg)
            file.let { image ->
                val bitmap = BitmapFactory.decodeFile(image.path)
                rotateImage(bitmap, pathImg).compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    FileOutputStream(image)
                )
                binding.ivProductImage.setImageBitmap(rotateImage(bitmap, pathImg))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpirationCheckerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnCamera.setOnClickListener {
                if (!checkImagePermission()) {
                    ActivityCompat.requestPermissions(
                        this@ExpirationCheckerActivity,
                        REQUIRED_CAMERA_PERMISS,
                        REQUEST_CODE_PERMISS
                    )
                } else {
                    startCamera()
                }
            }

            btnGallery.setOnClickListener {
                openGallery()
            }
        }

        ViewUtils.setupFullScreen(this)
    }

    private fun rotateImage(bitmap: Bitmap, path: String): Bitmap {
        val orientation = ExifInterface(path).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
        }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val customTempFile = File.createTempFile(
            SimpleDateFormat(
                "dd-MMM-yyyy",
                Locale.US
            ).format(System.currentTimeMillis()), ".jpg", storageDir
        )
        customTempFile.also {
            pathImg = it.absolutePath
            intent.putExtra(
                MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                    this@ExpirationCheckerActivity,
                    "com.alvintio.pedulipangan.fileprovider",
                    it
                )
            )
            cameraLauncher.launch(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun checkImagePermission() = REQUIRED_CAMERA_PERMISS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}
