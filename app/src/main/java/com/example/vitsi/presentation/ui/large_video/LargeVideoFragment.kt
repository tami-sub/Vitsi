package com.example.vitsi.presentation.ui.large_video

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vitsi.R
import com.example.vitsi.databinding.LargeVideoLayoutBinding
import com.example.vitsi.models.video.RemoteVideo
import com.example.vitsi.presentation.exoplayer.Player
import com.example.vitsi.presentation.ui.components.video.MainLargeVideo
import com.example.vitsi.repo.network.user.DefaultUserRepo
import com.example.vitsi.repo.network.videos.DefaultVideosRepo
import com.example.vitsi.utils.BottomNavViewUtils
import com.example.vitsi.utils.BottomNavViewUtils.hideBottomNavBar
import com.example.vitsi.utils.BottomNavViewUtils.showBottomNavBar
import com.example.vitsi.utils.SystemBarColors
import com.example.vitsi.utils.ViewUtils
import com.example.vitsi.utils.architecture.BaseFragment


class LargeVideoFragment : BaseFragment(R.layout.large_video_layout) {

    private lateinit var binding: LargeVideoLayoutBinding
    private lateinit var remoteVideo: RemoteVideo
    private val args by navArgs<LargeVideoFragmentArgs>()

    private val mainLargeVideo by lazy {
        MainLargeVideo(
            scope = lifecycleScope,
            lifecycle = viewLifecycleOwner.lifecycle,
            binding = binding,
            userRepo = DefaultUserRepo(),
            videosRepo = DefaultVideosRepo(),
            onPersonIconClicked = {
                findNavController().navigate(
                    LargeVideoFragmentDirections
                        .actionLargeVideoFragmentToProfileWithAccountFragment(remoteVideo.authorUid)
                )
            },
            onVideoEnded = { player ->
                player.restartPlayer()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // This is not included in setUpLayout() since mainLargeVideo requires that binding to initialized
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            isVideoLiked = mainLargeVideo.isVideoLiked
        }
        remoteVideo = args.remoteVideo
        mainLargeVideo.init(remoteVideo)
    }

    override fun setUpLayout() {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavBar(activity)
        ViewUtils.changeSystemBars(activity, SystemBarColors.DARK)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    
}