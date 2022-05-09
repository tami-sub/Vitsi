package com.example.vitsi.presentation.ui.auth.sign_up

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.vitsi.*
import com.example.vitsi.R
import com.example.vitsi.databinding.SignUpPageBinding
import com.example.vitsi.models.sign_up.GoogleBody
import com.example.vitsi.utils.BottomNavViewUtils
import com.example.vitsi.utils.architecture.BaseFragment
import com.google.firebase.auth.*
import kotlinx.coroutines.launch

class SignUpFragment : BaseFragment(R.layout.sign_up_page) {

    lateinit var binding: SignUpPageBinding

    override val viewModel by viewModels<SignUpViewModel>()

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
        binding = SignUpPageBinding.bind(requireView())
    }

    override fun setUpClickListeners() {
        binding.signUpCancelBtn.setOnClickListener { findNavController().popBackStack() }

        binding.logInGoogleBtn.setOnClickListener { findNavController().navigate(R.id.action_signUpFragment_to_loginFragment) }

        binding.signUpGoogleBtn.setOnClickListener {
            viewModel.googleAuthRepo.doGoogleAuth(requireContext(), launcher)
        }
    }

    override fun setUpLiveData() {
        viewModel.liveCredential.observe(viewLifecycleOwner) { liveCredential ->
            if (liveCredential == null) return@observe

            // Since the body is used in providing a name and a profile picture, we should not be afraid if the values are null
            // since we are performing a null check in CreateUserNameViewModel
            val googleAccount = viewModel.googleAccount
            val googleBody = GoogleBody(googleAccount?.displayName, googleAccount?.photoUrl?.toString())

            findNavController().navigate(
                SignUpFragmentDirections.actionSignUpFragmentToCreateUsernameFragment(
                    liveCredential,
                    googleBody,
                    null
                )
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}