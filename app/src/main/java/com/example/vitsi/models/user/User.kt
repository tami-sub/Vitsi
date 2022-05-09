package com.example.vitsi.models.user

data class User(
    var username: String,
    var totalLikes: Long,
    var profilePictureUrl: String?,
    val uid: String,
    var showLikedAudios: Boolean = false
) {
    constructor() : this("", 0, null, "")
}