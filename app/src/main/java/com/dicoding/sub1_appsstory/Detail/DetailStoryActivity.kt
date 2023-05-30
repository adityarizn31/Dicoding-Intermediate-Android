package com.dicoding.sub1_appsstory.Detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.dicoding.sub1_appsstory.Data.Story
import com.dicoding.sub1_appsstory.RemoteData.Result
import com.dicoding.sub1_appsstory.Utils.DateConverter
import com.dicoding.sub1_appsstory.ViewModel.MainViewModel
import com.dicoding.sub1_appsstory.ViewModel.ViewModelFactory
import com.dicoding.sub1_appsstory.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33) {
            val id = intent.getStringExtra("id")
            if (id != null) setContent(mainViewModel, id)
        } else {
            @Suppress("DEPRECATION")
            val id = intent.getStringExtra("id") as String
            setContent(mainViewModel, id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setContent(mainViewModel: MainViewModel, id: String) {
        val token = getToken()
        if (token != null) {
            mainViewModel.getStoryDetail("Bearer $token", id)
                .observe(this@DetailStoryActivity) { detail ->
                    if (detail != null) {
                        when (detail) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                val data = detail.data.story
                                if (data != null) {
                                    bindingData(data)
                                }
                            }
                            is Result.Error -> {
                                Toast.makeText(this, detail.error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
        }
    }

    private fun getToken(): String? {
        return mainViewModel.getPreference(this).value
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindingData(data: Story) {
        Glide.with(this)
            .load(data.photoUrl)
            .into(binding.photoIV)
        binding.nameTv.text = data.name
        binding.desc.text = data.description

        if (data.createdAt != null) {
            binding.date.text = DateConverter(data.createdAt)
        }
    }
}