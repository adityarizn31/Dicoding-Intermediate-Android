package com.dicoding.sub1_appsstory.RemoteData

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.sub1_appsstory.Data.LoginResp
import com.dicoding.sub1_appsstory.Data.DetailStoryResp
import com.dicoding.sub1_appsstory.Data.GetStoryResp
import com.dicoding.sub1_appsstory.Data.ReceiveResp
import com.dicoding.sub1_appsstory.Data.Register
import com.dicoding.sub1_appsstory.LocalData.PreferenceSetting
import com.dicoding.sub1_appsstory.Retrofit.ApiService
import com.dicoding.sub1_appsstory.Data.Login
import com.dicoding.sub1_appsstory.RemoteData.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException

class MainRepository private constructor(
    private val apiService: ApiService,
) {

    suspend fun registerUser(
        nama: String,
        password: String,
        email: String
    ): Result<ReceiveResp> {
        return try {
            val response = apiService.register(Register(nama, email, password))
            Result.Success(response)

        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            // Parse the error message from the error body
            val jsonObject = JSONObject(error!!)
            val errorMessage = jsonObject.getString("message")
            // Handle the error message
            Result.Error(errorMessage)
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

    fun loginUser(email: String, password: String): LiveData<Result<LoginResp>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(Login(email, password))
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStory(token: String): LiveData<Result<GetStoryResp>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStrories(token)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStoryDetail(token: String, id: String): LiveData<Result<DetailStoryResp>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getStroryDetail(token, id)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    suspend fun addNewStory(
        token: String,
        image: MultipartBody.Part,
        desc: RequestBody
    ): Result<ReceiveResp> {
        return try {
            val response = apiService.sendStory(token, image, desc)
            Result.Success(response)

        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            // Parse the error message from the error body
            val jsonObject = JSONObject(error!!)
            val errorMessage = jsonObject.getString("message")
            // Handle the error message
            Result.Error(errorMessage)
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }


    fun savePreference(token: String, context: Context) {
        val settingPreference = PreferenceSetting(context)
        settingPreference.setUser(token)
    }

    fun getPreference(context: Context): String? {
        val settingPreference = PreferenceSetting(context)
        return settingPreference.getUser()
    }

    companion object {
        @Volatile
        private var instance: MainRepository? = null
        fun getInstance(
            apiService: ApiService
        ): MainRepository =
            instance ?: synchronized(this) {
                instance ?: MainRepository(apiService)
            }.also { instance = it }
    }

}