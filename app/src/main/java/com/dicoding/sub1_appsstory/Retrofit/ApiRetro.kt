package com.dicoding.sub1_appsstory.Retrofit

import android.content.Context
import com.dicoding.sub1_appsstory.RemoteData.MainRepository

object ApiRetro {
    fun provideRepository(context: Context): MainRepository {
        val ApiSer = ApiConfig.getApiService()
        return MainRepository.getInstance(ApiSer)
    }
}