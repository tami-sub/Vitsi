package com.example.vitsi.presentation.ui.large_video

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vitsi.R
import com.example.vitsi.databinding.LargeAudioLayoutBinding
import com.example.vitsi.domain.audio.RemoteAudio
import com.example.vitsi.presentation.ui.components.MainLargeAudio
import com.example.vitsi.data.network.user.DefaultUserRepo
import com.example.vitsi.data.network.videos.DefaultVideosRepo
import com.example.vitsi.utils.BottomNavViewUtils.hideBottomNavBar
import com.example.vitsi.utils.SystemBarColors
import com.example.vitsi.utils.ViewUtils
import com.example.vitsi.presentation.architecture.BaseFragment


class LargeAudioFragment : BaseFragment(R.layout.large_audio_layout) {

    private lateinit var binding: LargeAudioLayoutBinding
    private lateinit var remoteAudio: RemoteAudio
    private val args by navArgs<LargeAudioFragmentArgs>()

    private val mainLargeVideo by lazy {
        MainLargeAudio(
            scope = lifecycleScope,
            lifecycle = viewLifecycleOwner.lifecycle,
            binding = binding,
            userRepo = DefaultUserRepo(),
            videosRepo = DefaultVideosRepo(),
            onPersonIconClicked = {
                findNavController().navigate(
                    LargeAudioFragmentDirections.actionLargeaudioFragmentToProfileWithAccountFragment(remoteAudio.authorUid)
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
        remoteAudio = args.remoteAudio
        mainLargeVideo.init(remoteAudio)
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