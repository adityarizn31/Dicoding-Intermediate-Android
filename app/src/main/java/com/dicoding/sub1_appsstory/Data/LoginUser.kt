package com.dicoding.sub1_appsstory.Data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginUser(
    var email: String?,
    var password: String?,
) : Parcelable
