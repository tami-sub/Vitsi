package com.example.vitsi.presentation.ui.auth.log_in

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.vitsi.R
import com.example.vitsi.databinding.FragmentLogInBinding
import com.example.vitsi.utils.BottomNavViewUtils
import com.example.vitsi.utils.architecture.BaseFragment

class LoginFragment : BaseFragment(R.layout.fragment_log_in) {

    private lateinit var binding: FragmentLogInBinding
    override val viewModel by viewModels<LogInViewModel>()

    // ActivityResultLauncher for Google Sign Up
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.googleAuthRepo.handleGoogleOnResult(result?.data)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BottomNavViewUtils.hideBottomNavBar(activity)

    }

    override fun setUpLayout() {
        binding = FragmentLogInBinding.bind(requireView())
    }

    override fun setUpClickListeners() {
        binding.logInCancelBtn.setOnClickListener { findNavController().popBackStack() }
        binding.logInGoogleBtn.setOnClickListener {
            viewModel.googleAuthRepo.doGoogleAuth(requireContext(), launcher)
        }
    }

    override fun setUpLiveData() {
        viewModel.navigateToMyProfile.observe(viewLifecycleOwner) { navigateToMyProfile ->
            if (navigateToMyProfile == true) {
                findNavController().navigate(
                    R.id.meFragment,
                    null,
                    navOptions { popUpTo = R.id.homeFragment }
                )
                viewModel.resetNavigateToMyProfile()
            }
        }
    }

}