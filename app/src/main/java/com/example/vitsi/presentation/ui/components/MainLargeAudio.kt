package com.example.vitsi.presentation.ui.components

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.lifecycle.*
import com.example.vitsi.domain.succeeded
import com.example.vitsi.domain.user.User
import com.example.vitsi.domain.audio.RemoteAudio
import com.example.vitsi.presentation.exoplayer.Player
import com.example.vitsi.data.network.user.UserRepo
import com.example.vitsi.data.network.videos.VideosRepo
import com.example.vitsi.utils.NumbersUtils
import com.bumptech.glide.Glide
import com.example.vitsi.databinding.LargeAudioLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainLargeAudio(
    private val scope: CoroutineScope,
    private val lifecycle: Lifecycle,
    private val binding: LargeAudioLayoutBinding,
    private val userRepo: UserRepo,
    private val videosRepo: VideosRepo,
    private val onPersonIconClicked: (String) -> Unit,
    private val onVideoEnded: (Player) -> Unit,
) {
    var author: User? = null
    var player: Player? = null

    private var likeCount = 0

    private val _isVideoLiked = MutableLiveData(false)
    val isVideoLiked: LiveData<Boolean> = _isVideoLiked

    fun init(remoteAudio: RemoteAudio) {
        scope.launch {
            createProfile(remoteAudio)
            createPlayer(remoteAudio)
            createVideoInfo(remoteAudio)
            setOnClickListeners(remoteAudio)
            enableDoubleTap(remoteAudio)

            isVideoLiked(remoteAudio)
        }
    }

    private fun createVideoInfo(remoteAudio: RemoteAudio) {
        likeCount = remoteAudio.likes.toInt()
        binding.totalVideoLikes.text = NumbersUtils.formatCount(likeCount)
    }

    private suspend fun createProfile(remoteAudio: RemoteAudio) {
        author = userRepo.getUserProfile(remoteAudio.authorUid).tryData()

        binding.authorUsername.text = author?.username?.let { "@${it}" } ?: "@..."
        Glide.with(binding.root).load(author?.profilePictureUrl).into(binding.authorIcon)
        binding.videoDescription.text = remoteAudio.description ?: "#NoDescription"
    }

    private fun createPlayer(remoteAudio: RemoteAudio) {
        player = Player(
            simpleExoplayerView = binding.simpleExoPlayerView,
            playBtn = binding.playBtn,
            context = binding.root.context,
            url = remoteAudio.url,
            onVideoEnded = { player -> onVideoEnded(player) }
        )
        lifecycle.addObserver(player!!)
        player?.init()
    }

    private fun setOnClickListeners(remoteAudio: RemoteAudio) {
        // TODO: Once the button is clicked, let's show a small pop-up layout that tells him/her to sign up or login
        if (userRepo.doesDeviceHaveAnAccount()) {
            binding.likeVideoIcon.setOnClickListener { likeOrUnlikeVideo(remoteAudio) }
        }
        binding.authorIcon.setOnClickListener { onPersonIconClicked(remoteAudio.authorUid) }
    }

    private fun enableDoubleTap(remoteAudio: RemoteAudio) {
        val gd = GestureDetector(binding.root.context, object: GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                player?.changePlayerState()
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                likeOrUnlikeVideo(remoteAudio)
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent?) = true
        })
        binding.simpleExoPlayerView.setOnTouchListener { view, event ->
            view.performClick()
            return@setOnTouchListener gd.onTouchEvent(event)
        }
    }

    private fun likeOrUnlikeVideo(remoteAudio: RemoteAudio) {
        scope.launch {
            // Change the heart icon
            val shouldLike =
                isVideoLiked.value != true // Simply put if the user already likes the video, dislike it otherwise like it
            _isVideoLiked.value = shouldLike

            // Change like count
            changeLikeCount(shouldLike)

            videosRepo.likeOrUnlikeVideo(
                audioId = remoteAudio.audioId,
                authorId = remoteAudio.authorUid,
                shouldLike = shouldLike
            )
        }
    }

    private fun changeLikeCount(shouldLike: Boolean) {
        if (shouldLike) likeCount++ else likeCount--
        binding.totalVideoLikes.text = NumbersUtils.formatCount(likeCount)
    }

    /**
     * Sets isVideoLiked to true if the user likes the video
     *
     * @param remoteAudio the video currently being displayed to the user
     */
    private suspend fun isVideoLiked(remoteAudio: RemoteAudio) {
        val isLiked = videosRepo.isVideoLiked(remoteAudio.audioId)
        _isVideoLiked.value = isLiked.succeeded && isLiked.forceData()
    }

    fun destroy() {
        player?.let {
            it.stopPlayer()
            lifecycle.removeObserver(it)
            player = null
        }
    }
}