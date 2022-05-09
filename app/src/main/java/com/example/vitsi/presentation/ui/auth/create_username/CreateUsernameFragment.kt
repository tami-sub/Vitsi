package com.example.vitsi.presentation.ui.auth.create_username

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.example.vitsi.*
import com.example.vitsi.databinding.FragmentCreateUsernameBinding
import com.example.vitsi.models.upload.Progress
import com.example.vitsi.utils.KeyboardUtils
import com.example.vitsi.utils.ResUtils
import com.example.vitsi.utils.architecture.BaseFragment
import kotlinx.coroutines.launch

class CreateUsernameFragment : BaseFragment(R.layout.fragment_create_username) {

    override val viewModel by viewModels<CreateUsernameViewModel>()
    private val args by navArgs<CreateUsernameFragmentArgs>()

    private lateinit var binding: FragmentCreateUsernameBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setUp(args)
    }

    override fun setUpLayout() {
        binding = FragmentCreateUsernameBinding.bind(requireView()).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
    }

    override fun setUpClickListeners() {
        binding.signUpBtn.setOnClickListener {
            KeyboardUtils.hide(binding.usernameInput)
            viewModel.completeSignIn()
        }

        // Since the user hasn't actually given us a username, let's generate one
        binding.skipBtn.setOnClickListener {
            KeyboardUtils.hide(binding.usernameInput)
            lifecycleScope.launch { viewModel.generateRandomName() }
            viewModel.completeSignIn()
        }
    }

    override fun setUpLiveData() {
        viewModel.errorTextRes.observe(viewLifecycleOwner) { errorTextRes ->
            binding.liveStatusText.text = getString(errorTextRes ?: return@observe)
        }
        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            when (progress) {
                Progress.IDLE -> changeButtonStatus(makeActive = true)
                Progress.ACTIVE -> changeButtonStatus(makeActive = false)
                Progress.DONE -> navigateHome()
                Progress.FAILED -> { ResUtils.showSnackBar(requireView(), R.string.error_occurred_during_sign_up) }
                null -> throw Exception("The progress was null")
            }
        }
    }

    // Whether to make the button pink and active or grey and disabled
    private fun changeButtonStatus(makeActive: Boolean) {
        binding.signUpBtn.isClickable = makeActive
        binding.signUpBtn.setTextColor(
            if (makeActive) Color.WHITE
            else ResUtils.getResColor(requireContext(), R.color.light_grey)
        )
        binding.signUpBtn.backgroundTintList =
            ResourcesCompat.getColorStateList(
                resources,
                if(makeActive) R.color.pinkBtnBackground else R.color.light_grey,
                null
            )
    }

    // Navigate home and remove every fragment on the way since sign up is complete
    private fun navigateHome() {
        findNavController().navigate(
            CreateUsernameFragmentDirections
                .actionCreateUsernameFragmentToHomeFragment(),
            navOptions {
                popUpTo = R.id.homeFragment
            }
        )
    }
}