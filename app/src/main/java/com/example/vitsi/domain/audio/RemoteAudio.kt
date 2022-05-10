package com.example.vitsi.domain.audio

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RemoteAudio(
    var url: String,
    var description: String?,
    var duration: Long,
    var audioId: String,
    var dateCreated: Long,
    var likes: Long = 0,
    var views: Long = 0,
    var authorUid: String
) : Parcelable {
    constructor() : this("", null, -1, "", -1, -1, -1, "")
}