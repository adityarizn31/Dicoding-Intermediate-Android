package com.dicoding.sub1_appsstory.Story

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.sub1_appsstory.Camera.CameraActivity
import com.dicoding.sub1_appsstory.MainActivity
import com.dicoding.sub1_appsstory.R
import com.dicoding.sub1_appsstory.Utils.reduceFileImage
import com.dicoding.sub1_appsstory.Utils.rotateFile
import com.dicoding.sub1_appsstory.Utils.uriToFile
import com.dicoding.sub1_appsstory.ViewModel.MainViewModel
import com.dicoding.sub1_appsstory.ViewModel.ViewModelFactory
import com.dicoding.sub1_appsstory.databinding.ActivityAddStoryBinding
import com.dicoding.sub1_appsstory.RemoteData.Result
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity(), MenuItem.OnMenuItemClickListener {

    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Mohon Maaf Anda Tidak Mendapatkan Permissions !!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    // Penggunaan Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_addnewstory, menu)
        val OpenCam = menu?.findItem(R.id.open_camera)
        val OpenGall = menu?.findItem(R.id.open_gallery)
        val SendStor = menu?.findItem(R.id.send)

        OpenCam?.setOnMenuItemClickListener(this)
        OpenGall?.setOnMenuItemClickListener(this)
        SendStor?.setOnMenuItemClickListener(this)

        return true
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.open_camera -> {
                startCameraX()
                true
            }
            R.id.open_gallery -> {
                startGallery()
                true
            }
            R.id.send -> {
                val tokenSend = mainViewModel.getPreference(this).value
                val edDesc = binding.edDeskripsi
                if (tokenSend != null) {
                    uploadImage(edDesc, tokenSend)
                }
                true
            }
            else -> false
        }
    }

    // Open Cam
    private fun startCameraX() {
        val move = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(move)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.preview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    //Open Gallery
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
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.preview.setImageURI(uri)
            }
        }
    }


    //Send Story ke Server API
    private fun uploadImage(Getdesc: EditText, token: String) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val desc = Getdesc.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            mainViewModel.addNewStory("Bearer $token", imageMultipart, desc)
                .observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            val response = result.data
                            val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is Result.Error -> {
                            val errorMessage = result.error
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }


        } else {
            Toast.makeText(
                this@AddStoryActivity,
                "Masukan File Gambar atau Foto Terlebih dahulu",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}