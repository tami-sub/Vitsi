package com.example.vitsi.models.video

/**
 * This enum represents the three tabs in the [com.example.vitsi.presentation.ui.profile.with_account.ProfileWithAccountFragment] fragment
 * allowing us to re-use one fragment instead of three
 */
enum class VideoType {
    LIKED {
        override fun toString(): String = "liked-audios"
    },
    PRIVATE {
        override fun toString(): String = "private-audios"
    },
    PUBLIC {
        override fun toString(): String = "public-audios"
    }
}