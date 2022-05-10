package com.example.vitsi.presentation.ui.home.large_video_group

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.example.vitsi.R
import com.example.vitsi.databinding.LargeAudioLayoutBinding
import com.example.vitsi.domain.audio.RemoteAudio
import com.example.vitsi.presentation.ui.components.MainLargeAudio
import com.example.vitsi.data.network.user.UserRepo
import com.example.vitsi.data.network.videos.VideosRepo
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder
import kotlinx.coroutines.CoroutineScope

class LargeVideoGroup(
    private val scope: CoroutineScope,
    private val lifecycleOwner: LifecycleOwner,
    private val userRepo: UserRepo,
    private val videosRepo: VideosRepo,
    private val remoteAudio: RemoteAudio,
    private val onPersonIconClicked: (String) -> Unit,
    private val onVideoEnded: (LargeVideoGroup) -> Unit
) : BindableItem<LargeAudioLayoutBinding>() {

    private lateinit var mainLargeVideo: MainLargeAudio

    override fun bind(binding: LargeAudioLayoutBinding, position: Int) {
        mainLargeVideo = MainLargeAudio(
            scope = scope,
            lifecycle = lifecycleOwner.lifecycle,
            binding = binding,
            userRepo = userRepo,
            videosRepo = videosRepo,
            onPersonIconClicked = onPersonIconClicked,
            onVideoEnded = { onVideoEnded(this) },
        )

        binding.lifecycleOwner = lifecycleOwner
        binding.isVideoLiked = mainLargeVideo.isVideoLiked
    }

    override fun onViewAttachedToWindow(viewHolder: GroupieViewHolder<LargeAudioLayoutBinding>) {
        super.onViewAttachedToWindow(viewHolder)
        mainLargeVideo.init(remoteAudio)
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

    override fun onViewDetachedFromWindow(viewHolder: GroupieViewHolder<LargeAudioLayoutBinding>) {
        super.onViewDetachedFromWindow(viewHolder)
        mainLargeVideo.destroy()
        lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
    }

    override fun initializeViewBinding(view: View) =
        LargeAudioLayoutBinding.bind(view)

    override fun getLayout() = R.layout.large_audio_layout

    private val lifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            mainLargeVideo.player?.pausePlayer()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            mainLargeVideo.player?.pausePlayer()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            mainLargeVideo.destroy()
        }
    }
}