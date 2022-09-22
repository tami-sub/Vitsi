package com.example.vitsi.domain.user

data class User(
    var username: String = "",
    var totalLikes: Long = 0,
    var profilePictureUrl: String? = null,
    val uid: String = "",
    var showLikedAudios: Boolean = false
)