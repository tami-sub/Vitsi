package com.example.vitsi.presentation.ui.profile.with_account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.vitsi.*
import com.example.vitsi.databinding.FragmentProfileWithAccountBinding
import com.example.vitsi.utils.BottomNavViewUtils.showBottomNavBar
import com.example.vitsi.utils.ImageUtils.loadGlideImage
import com.example.vitsi.utils.SystemBarColors
import com.example.vitsi.utils.ViewUtils
import com.example.vitsi.presentation.architecture.BaseFragment
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
    }

    private val onPageChangedCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            Timber.d("Callback position is $position")
        }
    }


    override fun setUpLayout() {
        binding = FragmentProfileWithAccountBinding.bind(requireView())
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
                Firebase.auth.signOut()
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
}