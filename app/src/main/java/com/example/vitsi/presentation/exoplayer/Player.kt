package com.example.vitsi.presentation.exoplayer

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView


class Player(
    private val simpleExoplayerView: PlayerView,
    private val playBtn: ImageView,
    private val context: Context,
    private val url: String?,
    private val onVideoEnded: (com.example.vitsi.presentation.exoplayer.Player) -> Unit
): LifecycleObserver {
    private var playbackPosition = 0L
    private var simpleExoPlayer: SimpleExoPlayer? = null

    private var isCreated = false
    private var isPlaying = false

    fun init() {
        createPlayer()

        playBtn.setOnClickListener {
            resumePlayer()
        }
    }

    private fun createPlayer() {
        isCreated = true

        simpleExoPlayer = SimpleExoPlayer.Builder(context)
            .setUseLazyPreparation(true)
            .build()

        simpleExoPlayer?.addListener(playerListener)
        simpleExoPlayer?.setMediaItem(MediaItem.fromUri(Uri.parse(url)))

        simpleExoplayerView.player = simpleExoPlayer
        simpleExoplayerView.setShutterBackgroundColor(Color.TRANSPARENT)
        simpleExoplayerView.requestFocus()

        simpleExoPlayer?.prepare()
        resumePlayer()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resumePlayer() {
        if (isCreated && !isPlaying) {
            isPlaying = true
            playBtn.visibility = View.GONE
            simpleExoPlayer?.seekTo(playbackPosition)
            simpleExoPlayer?.play()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pausePlayer() {
        if (isCreated && isPlaying) {
            isPlaying = false
            playBtn.visibility = View.VISIBLE
            playbackPosition = simpleExoPlayer!!.currentPosition
            simpleExoPlayer?.pause()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stopPlayer() {
        if (isCreated) {
            pausePlayer()
            simpleExoPlayer?.release()
            simpleExoPlayer = null
            isCreated = false
        }
    }

    fun restartPlayer() {
        if (isCreated) {
            playbackPosition = 0
            isPlaying = true
            simpleExoPlayer?.seekTo(0, playbackPosition)
//            simpleExoPlayer?.playWhenReady = true
            simpleExoPlayer?.play()
        }
    }

    // Pauses or resumes the video
    fun changePlayerState() {
        if (isCreated) {
            if (isPlaying) {
                pausePlayer()
            } else {
                resumePlayer()
            }
        }
    }

    private val playerListener = object : Player.EventListener {
        override fun onPlayerError(error: ExoPlaybackException) {
            Log.d("joka", error.toString())
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            simpleExoplayerView.hideController()
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                }
                Player.STATE_READY -> {
                }
                Player.STATE_ENDED -> {
                    onVideoEnded(this@Player)
                }
                Player.STATE_IDLE -> {
                }
            }
        }
    }
}