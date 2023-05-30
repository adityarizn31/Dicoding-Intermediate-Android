package com.dicoding.sub1_appsstory.Data

import com.google.gson.annotations.SerializedName

data class ReceiveResp(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)