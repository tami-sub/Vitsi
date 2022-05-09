package com.example.vitsi.models.video

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RemoteVideo(
    var url: String,
    var description: String?,
    var tags: Map<String, String>,
    var duration: Long,
    var audioId: String,
    var dateCreated: Long,
    var likes: Long = 0,
    var views: Long = 0,
    var authorUid: String
) : Parcelable {
    constructor() : this("", null, mapOf(), -1, "", -1, -1, -1, "")
}