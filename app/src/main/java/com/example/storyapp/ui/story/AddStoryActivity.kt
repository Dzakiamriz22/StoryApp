package com.example.storyapp.ui.story

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.storyapp.data.model.AddStoryResponse
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.data.remote.ApiClient
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            showToast("Permission request denied")
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val bitmap = BitmapFactory.decodeFile(myFile.path)
            binding.previewImage.setImageBitmap(bitmap)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                getFile = uriToFile(uri, this)
                binding.previewImage.setImageURI(uri)
                animatePreviewImage()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Story"

        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadStory() }
    }

    private fun animatePreviewImage() {
        binding.previewImage.alpha = 0f
        binding.previewImage.animate().alpha(1f).setDuration(500).start()
    }

    private fun startCamera() {
        if (!hasPermission(Manifest.permission.CAMERA)) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)?.let {
            createTempFile().also { file ->
                val photoURI: Uri = FileProvider.getUriForFile(this, "com.dicoding.submission1int.fileprovider", file)
                currentPhotoPath = file.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                cameraLauncher.launch(intent)
            }
        }
    }

    private fun createTempFile(): File {
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private val timeStamp: String = SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(System.currentTimeMillis())

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        galleryLauncher.launch(chooser)
    }

    private fun uploadStory() {
        if (getFile == null) {
            showToast("Please select an image")
            return
        }

        val description = binding.edAddDescription.text.toString()
        if (description.isEmpty()) {
            binding.edAddDescription.error = "Description is required"
            return
        }

        val file = reduceFileImage(getFile as File)
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

        val token = sharedPreferences.getString("token", "") ?: ""
        showLoading(true)

        ApiClient.apiInterface.addStory("Bearer $token", imageMultipart, descriptionRequestBody).enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(call: Call<AddStoryResponse>, response: Response<AddStoryResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    showToast("Story uploaded successfully")
                    finish()
                } else {
                    showToast("Failed to upload story")
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                showLoading(false)
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

}
