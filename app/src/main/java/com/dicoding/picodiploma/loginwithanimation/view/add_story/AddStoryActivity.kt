package com.dicoding.picodiploma.loginwithanimation.view.add_story

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.createCustomTempFile
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.uriToFile
import com.dicoding.picodiploma.loginwithanimation.utility.LoadingDialog
import com.dicoding.picodiploma.loginwithanimation.utility.NetworkResponse
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.view.main.dataStore
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private var imageFile: File? = null

    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        binding.buttonCamera.setOnClickListener { startTakePhoto() }
        binding.buttonGalery.setOnClickListener { startGallery() }
        binding.btUpload.setOnClickListener {
            lifecycleScope.launchWhenCreated{
                uploadImage()
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.dicoding.picodiploma.mycamera",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            val result =  BitmapFactory.decodeFile(myFile.path)
            imageFile = myFile
            binding.ivCoverAdd.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            imageFile = myFile
            binding.ivCoverAdd.setImageURI(selectedImg)
        }
    }

    private suspend fun uploadImage() {
        val loading = LoadingDialog(this)
        loading.startLoading()
        if (imageFile != null){
            mainViewModel.getUser().collect { user ->
                if (user.isLogin) {
                    val file = reduceFileImage(imageFile as File)
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo", file.name, requestImageFile
                    )
                    binding.apply {
                        val description = descriptionEditText.text?.trim().toString().toRequestBody("text/plain".toMediaType())
                        val token = "Bearer ${user.token}"
                        addStoryViewModel.uploadStory(token, imageMultipart, description).observe(this@AddStoryActivity){ addData ->
                            if (addData != null){
                                when (addData){
                                    is NetworkResponse.Success -> {
                                        loading.dismissLoading()
                                        Log.d("Upload Story", addData.data.message)
                                        AlertDialog.Builder(this@AddStoryActivity).apply {
                                            setTitle("Yeah!")
                                            setMessage("Data Storymu Berhasil Di Upload")
                                            setPositiveButton("Oke") { _, _ ->
                                                val intent = Intent(context, MainActivity::class.java)
                                                intent.putExtra(MainActivity.SUCCESS_UPLOAD, true)
                                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                startActivity(intent)
                                                finish()
                                            }
                                            create()
                                            show()
                                            loading.dismissLoading()
                                        }
                                    }
                                    is NetworkResponse.Loading -> {
                                        Log.d("Upload Story", addData.toString())
                                        Toast.makeText(this@AddStoryActivity, "Uploading...", Toast.LENGTH_SHORT).show()
                                    }
                                    is NetworkResponse.Error -> {
                                        Toast.makeText(this@AddStoryActivity, "Gagal Upload Story", Toast.LENGTH_SHORT).show()
                                        Log.d("Upload Story", addData.error)
                                        loading.dismissLoading()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}