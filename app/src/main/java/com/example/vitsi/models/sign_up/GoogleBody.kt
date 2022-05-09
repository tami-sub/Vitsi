package com.example.vitsi.models.sign_up

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoogleBody(val userName: String?, val profilePicture: String?): Parcelable
