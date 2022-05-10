package com.example.vitsi.domain.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalVideo(
    var filePath: String?,
    val duration: Long?,
    val dateCreated: String?
): Parcelable