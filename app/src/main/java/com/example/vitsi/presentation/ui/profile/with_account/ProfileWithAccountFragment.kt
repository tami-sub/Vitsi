package com.example.vitsi.presentation.ui.profile.with_account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.vitsi.*
import com.example.vitsi.databinding.FragmentProfileWithAccountBinding
import com.example.vitsi.models.video.VideoType
import com.example.vitsi.presentation.ui.profile.with_account.tab.ProfileVideoTab
import com.example.vitsi.utils.BottomNavViewUtils.showBottomNavBar
import com.example.vitsi.utils.ImageUtils.loadGlideImage
import com.example.vitsi.utils.SystemBarColors
import com.example.vitsi.utils.ViewUtils
import com.example.vitsi.utils.architecture.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class ProfileWithAccountFragment : BaseFragment(R.layout.fragment_profile_with_account) {

    private lateinit var binding: FragmentProfileWithAccountBinding

    private val args by navArgs<ProfileWithAccountFragmentArgs>()
    override val viewModel by viewModels<ProfileWithAccountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchUser(args.uid)

        val tabConfigurationStrategy =
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                Timber.d("tab is $tab and tab position is $position")
                tab.text = getString(
                    when (position) {
                        0 -> R.string.my_audios
                        1 -> R.string.my_private_audios
                        2 -> R.string.my_liked_audios
                        else -> throw IndexOutOfBoundsException("position was $position")
                    }
                )
            }
        TabLayoutMediator(binding.profileTabLayout, binding.viewpager, tabConfigurationStrategy)
            .attach()
    }

    private val onPageChangedCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            Timber.d("Callback position is $position")
        }
    }


    override fun setUpLayout() {
        binding = FragmentProfileWithAccountBinding.bind(requireView())
        binding.viewpager.apply {
            adapter = MyFragmentStateAdapter(this@ProfileWithAccountFragment)
            registerOnPageChangeCallback(onPageChangedCallback)
        }
    }

    override fun setUpLiveData() {
        viewModel.profileUser.observe(viewLifecycleOwner) { profileUser ->
            profileUser?.let {
                binding.likesCountNumber.text = profileUser.totalLikes.toString()
                binding.userNickname.text = getString(R.string.author_username, profileUser.username)

                // Since the user can chose to stay without a profile picture, lets use the person icon
                // as a default.
                if (profileUser.profilePictureUrl == null)
                    loadGlideImage(binding.userPhoto, R.drawable.white_person_icon)
                else
                    loadGlideImage(binding.userPhoto, profileUser.profilePictureUrl)
            }
        }
    }

    override fun setUpClickListeners() {
        super.setUpClickListeners()
        if (args.uid == Firebase.auth.uid) {
            binding.editProfileBtn.setOnClickListener {
                // TODO: Navigate to EditeProfileFragment
            }
        }
    }

    override fun onStart() {
        super.onStart()
        ViewUtils.changeSystemBars(activity, SystemBarColors.WHITE)
    }

    override fun onResume() {
        super.onResume()
        showBottomNavBar(activity)
    }


    class MyFragmentStateAdapter(
        private val profileWithAccountFragment: ProfileWithAccountFragment
    ) : FragmentStateAdapter(profileWithAccountFragment) {

        /*
        If the profile Uid represents the current user, show him everything
        If the profile user wants to show their liked videos, show the current user the first two: public and liked videos
        Otherwise, show only the public videos
         */
        override fun getItemCount(): Int = when {
            profileWithAccountFragment.args.uid == Firebase.auth.uid -> 3
            profileWithAccountFragment.viewModel.profileUser.value?.showLikedAudios == true -> 2
            else -> 1
        }

        override fun createFragment(position: Int): Fragment {
            val uid = profileWithAccountFragment.args.uid
            return when (position) {
                0 -> ProfileVideoTab.getInstance(uid, VideoType.PUBLIC)
                1 -> ProfileVideoTab.getInstance(uid, VideoType.LIKED)
                2 -> ProfileVideoTab.getInstance(uid, VideoType.PRIVATE)
                else -> throw ArrayIndexOutOfBoundsException(position)
            }
        }
    }
}