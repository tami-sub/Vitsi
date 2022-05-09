package com.example.vitsi.presentation.ui.components.video

import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import androidx.lifecycle.*
import com.example.vitsi.databinding.LargeVideoLayoutBinding
import com.example.vitsi.models.succeeded
import com.example.vitsi.models.user.User
import com.example.vitsi.models.video.RemoteVideo
import com.example.vitsi.presentation.exoplayer.Player
import com.example.vitsi.repo.network.user.UserRepo
import com.example.vitsi.repo.network.videos.VideosRepo
import com.example.vitsi.utils.NumbersUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainLargeVideo(
    private val scope: CoroutineScope,
    private val lifecycle: Lifecycle,
    private val binding: LargeVideoLayoutBinding,
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

    fun init(remoteVideo: RemoteVideo) {
        scope.launch {
            createProfile(remoteVideo)
            createPlayer(remoteVideo)
            createVideoInfo(remoteVideo)
            setOnClickListeners(remoteVideo)
            enableDoubleTap(remoteVideo)

            isVideoLiked(remoteVideo)
        }
    }

    private fun createVideoInfo(remoteVideo: RemoteVideo) {
        likeCount = remoteVideo.likes.toInt()
        binding.totalVideoLikes.text = NumbersUtils.formatCount(likeCount)
    }

    private suspend fun createProfile(remoteVideo: RemoteVideo) {
        author = userRepo.getUserProfile(remoteVideo.authorUid).tryData()
        Timber.d("author is $author")

        binding.authorUsername.text = author?.username?.let { "@${it}" } ?: "@..."
        Glide.with(binding.root).load(author?.profilePictureUrl).into(binding.authorIcon)
        binding.videoDescription.text = remoteVideo.description ?: "#NoDescription"
    }

    private fun createPlayer(remoteVideo: RemoteVideo) {
        player = Player(
            simpleExoplayerView = binding.simpleExoPlayerView,
            playBtn = binding.playBtn,
            context = binding.root.context,
            url = remoteVideo.url,
            onVideoEnded = { player -> onVideoEnded(player) }
        )
        lifecycle.addObserver(player!!)
        player?.init()
    }

    private fun setOnClickListeners(remoteVideo: RemoteVideo) {
        // TODO: Once the button is clicked, let's show a small pop-up layout that tells him/her to sign up or login
        if (userRepo.doesDeviceHaveAnAccount()) {
            binding.likeVideoIcon.setOnClickListener { likeOrUnlikeVideo(remoteVideo) }
        }
        binding.authorIcon.setOnClickListener { onPersonIconClicked(remoteVideo.authorUid) }
    }

    private fun enableDoubleTap(remoteVideo: RemoteVideo) {
        val gd = GestureDetector(binding.root.context, object: GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                player?.changePlayerState()
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                likeOrUnlikeVideo(remoteVideo)
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent?) = true
        })
        binding.simpleExoPlayerView.setOnTouchListener { view, event ->
            view.performClick()
            return@setOnTouchListener gd.onTouchEvent(event)
        }
    }

    private fun likeOrUnlikeVideo(remoteVideo: RemoteVideo) {
        scope.launch {
            // Change the heart icon
            val shouldLike =
                isVideoLiked.value != true // Simply put if the user already likes the video, dislike it otherwise like it
            _isVideoLiked.value = shouldLike

            // Change like count
            changeLikeCount(shouldLike)

            videosRepo.likeOrUnlikeVideo(
                audioId = remoteVideo.audioId,
                authorId = remoteVideo.authorUid,
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
     * @param remoteVideo the video currently being displayed to the user
     */
    private suspend fun isVideoLiked(remoteVideo: RemoteVideo) {
        val isLiked = videosRepo.isVideoLiked(remoteVideo.audioId)
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