package com.example.vitsi.presentation.ui.home.large_video_group

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.example.vitsi.R
import com.example.vitsi.databinding.LargeVideoLayoutBinding
import com.example.vitsi.models.video.RemoteVideo
import com.example.vitsi.presentation.ui.components.video.MainLargeVideo
import com.example.vitsi.repo.network.user.UserRepo
import com.example.vitsi.repo.network.videos.VideosRepo
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder
import kotlinx.coroutines.CoroutineScope

class LargeVideoGroup(
    private val scope: CoroutineScope,
    private val lifecycleOwner: LifecycleOwner,
    private val userRepo: UserRepo,
    private val videosRepo: VideosRepo,
    private val remoteVideo: RemoteVideo,
    private val onPersonIconClicked: (String) -> Unit,
    private val onVideoEnded: (LargeVideoGroup) -> Unit
) : BindableItem<LargeVideoLayoutBinding>() {

    private lateinit var mainLargeVideo: MainLargeVideo

    override fun bind(binding: LargeVideoLayoutBinding, position: Int) {
        mainLargeVideo = MainLargeVideo(
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

    override fun onViewAttachedToWindow(viewHolder: GroupieViewHolder<LargeVideoLayoutBinding>) {
        super.onViewAttachedToWindow(viewHolder)
        mainLargeVideo.init(remoteVideo)
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

    override fun onViewDetachedFromWindow(viewHolder: GroupieViewHolder<LargeVideoLayoutBinding>) {
        super.onViewDetachedFromWindow(viewHolder)
        mainLargeVideo.destroy()
        lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
    }

    override fun initializeViewBinding(view: View) =
        LargeVideoLayoutBinding.bind(view)

    override fun getLayout() = R.layout.large_video_layout

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