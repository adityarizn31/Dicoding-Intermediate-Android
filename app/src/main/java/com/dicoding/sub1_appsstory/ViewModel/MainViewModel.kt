package com.dicoding.sub1_appsstory.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.sub1_appsstory.Data.ReceiveResp
import com.dicoding.sub1_appsstory.RemoteData.MainRepository
import com.dicoding.sub1_appsstory.RemoteData.Result
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val LiveDataResult = MutableLiveData<Result<ReceiveResp>>()
    private val token = MutableLiveData<String?>()

    fun registerNewUser(
        nama: String,
        password: String,
        email: String
    ): LiveData<Result<ReceiveResp>> {
        viewModelScope.launch {
            val result = mainRepository.registerUser(nama, password, email)
            LiveDataResult.value = result
        }
        return LiveDataResult
    }

    fun addNewStory(
        token: String,
        image: MultipartBody.Part,
        desc: RequestBody
    ) : LiveData<Result<ReceiveResp>> {
        viewModelScope.launch {
            val result = mainRepository.addNewStory(token, image, desc)
            LiveDataResult.value = result
        }
        return LiveDataResult
    }

    fun loginNewUser(
        email: String, password: String
    ) = mainRepository.loginUser(email, password)

    fun getStory(
        token: String
    ) = mainRepository.getStory(token)

    fun getPreference(
        context: Context
    ): LiveData<String?> {
        val DataToken = mainRepository.getPreference(context)
        token.value = DataToken
        return token
    }

    fun setPreference(
        token: String, context: Context
    ) = mainRepository.savePreference(token, context)

    fun getStoryDetail(
        token: String, id: String
    ) = mainRepository.getStoryDetail(token, id)
}