package com.example.vitsi.repo.network.videos

import com.example.vitsi.models.video.RemoteVideo
import com.example.vitsi.models.video.VideoType
import com.example.vitsi.repo.network.utils.FirePath
import com.example.vitsi.repo.network.utils.safeAccess
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

class DefaultVideosRepo : VideosRepo {
    private val realFire = Firebase.database
    private val firePath = FirePath()

    override suspend fun fetchRandomAudios() = safeAccess {

        realFire.getReference("audios").also { it.keepSynced(true) }
            .limitToFirst(12)
            .get()
            .await()
            .getValue<Map<String, RemoteVideo>>()
            ?.values
            ?.toList() ?: listOf()
    }

    override suspend fun fetchAudio(audioId: String) = safeAccess {
        realFire.getReference("audios")
            .child(audioId)
            .get()
            .await()
            .getValue<RemoteVideo>()
    }

    override suspend fun isVideoLiked(audioId: String) = safeAccess {
        realFire
            .getReference(firePath.getMyLikedAudios())
            .child(audioId)
            .get()
            .await()
            .exists()
    }

    override suspend fun likeOrUnlikeVideo(audioId: String, authorId: String, shouldLike: Boolean) {
        val myLikedVideos = realFire
            .getReference(firePath.getMyLikedAudios())
            .child(audioId)

        val videoRef = realFire
            .getReference(firePath.getAllAudiosPath())
            .child(audioId)
            .child("likes")

        val authorTotalLikesCountRef = realFire
            .getReference(firePath.getUserInfo(authorId))
            .child("totalLikes")


        myLikedVideos.setValue(if (shouldLike) audioId else null)

        var videoLikeCount = videoRef.get().await().getValue<Int>() ?: 0
        if (shouldLike) videoLikeCount++ else videoLikeCount--
        videoRef.setValue(videoLikeCount)

        var totalLikeCount = authorTotalLikesCountRef.get().await().getValue<Int>() ?: 0
        if (shouldLike) totalLikeCount++ else totalLikeCount--
        authorTotalLikesCountRef.setValue(totalLikeCount)
    }

    private fun getRemoteVideoFromLocalVideo(
        videoUrl: String,
        descriptionText: String,
        duration: Long?
    ) =
        RemoteVideo(
            url = videoUrl,
            description = descriptionText,
            duration = duration ?: 0,
            audioId = UUID.randomUUID().toString(),
            dateCreated = System.currentTimeMillis(),
            likes = 0,
            views = 0,
            authorUid = Firebase.auth.uid ?: ""
        )


    override suspend fun saveVideoToFireDB(
        isPrivate: Boolean,
        videoUrl: String,
        descriptionText: String,
        duration: Long?,
        onComplete: (Boolean) -> Unit
    ) {
        try {
            val videoType = if (isPrivate) VideoType.PRIVATE else VideoType.PUBLIC
            val remoteVideo =
                getRemoteVideoFromLocalVideo(videoUrl, descriptionText, duration)

            if (!isPrivate) {
                makeVideoPublic(remoteVideo)
            }
            saveVideoToMyAccount(videoType, remoteVideo)
            onComplete(true)
        } catch (e: Exception) {
            Timber.e(e)
            onComplete(false)
        }
    }

    private suspend fun saveVideoToMyAccount(
        videoType: VideoType,
        remoteVideo: RemoteVideo
    ) {
        realFire
            .getReference(firePath.getUserAudios(Firebase.auth.uid!!, videoType))
            .child(remoteVideo.audioId).setValue(remoteVideo.audioId)
            .await()
    }

    private suspend fun makeVideoPublic(
        remoteVideo: RemoteVideo
    ) {
        realFire
            .getReference(firePath.getAllAudiosPath())
            .child(remoteVideo.audioId).setValue(remoteVideo)
            .await()
    }

}