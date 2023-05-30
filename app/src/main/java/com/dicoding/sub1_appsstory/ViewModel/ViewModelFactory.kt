package com.dicoding.sub1_appsstory.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.sub1_appsstory.RemoteData.MainRepository
import com.dicoding.sub1_appsstory.Retrofit.ApiRetro

class ViewModelFactory private constructor(private val mainRepository: MainRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(ApiRetro.provideRepository(context))
        }.also { instance = it }
    }
}