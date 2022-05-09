package com.example.vitsi.presentation.ui.home

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.vitsi.R
import com.example.vitsi.databinding.FragmentHomeBinding
import com.example.vitsi.presentation.ui.home.large_video_group.LargeVideoGroup
import com.example.vitsi.utils.BottomNavViewUtils.changeNavBarColor
import com.example.vitsi.utils.BottomNavViewUtils.showBottomNavBar
import com.example.vitsi.utils.SystemBarColors
import com.example.vitsi.utils.ViewUtils.changeSystemBars
import com.example.vitsi.utils.ViewUtils.hideStatusBar
import com.example.vitsi.utils.architecture.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override val viewModel by viewModels<HomeViewModel>()
    private val snapHelper = PagerSnapHelper()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    override fun setUpLayout() {
        binding = FragmentHomeBinding.bind(requireView())
    }

    override fun setUpLiveData() {
        viewModel.listOfRemoteVideo.observe(viewLifecycleOwner) { listOfRemoteVideo ->
            val listOfGroup = listOfRemoteVideo?.map { remoteVideo ->
                val largeVideoGroup = LargeVideoGroup(
                    scope = lifecycleScope,
                    lifecycleOwner = viewLifecycleOwner,
                    userRepo = viewModel.userRepo,
                    videosRepo = viewModel.videosRepo,
                    remoteVideo = remoteVideo,
                    onPersonIconClicked = { uid ->
                        findNavController().navigate(
                            HomeFragmentDirections
                                .actionHomeFragmentToProfileWithAccountFragment(uid)
                        )
                    },
                    onVideoEnded = {
                        scrollDownToNextVideo(groupAdapter.getAdapterPosition(it))
                    }
                )

                largeVideoGroup
            } ?: listOf()

            groupAdapter.addAll(listOfGroup)
        }
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = groupAdapter
            it.addOnScrollListener(homeScrollListener)
            snapHelper.attachToRecyclerView(it)
        }
    }

    private fun scrollDownToNextVideo(currentPosition: Int) {
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        layoutManager.scrollToPosition(currentPosition + 1)
    }

    private val homeScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                val llm = (recyclerView.layoutManager as LinearLayoutManager)
                val lastVisiblePosition = llm.findLastVisibleItemPosition()

                // Checks if we are reaching towards the end of the list
                if (groupAdapter.itemCount - lastVisiblePosition <= 5) {
                    viewModel.fetchVideos()
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavBar(activity)
        changeNavBarColor(activity, SystemBarColors.DARK)
        changeSystemBars(activity, SystemBarColors.DARK)
        hideStatusBar(requireActivity())
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}