package com.example.vitsi.presentation.ui.profile.without_account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vitsi.R
import com.example.vitsi.databinding.FragmentProfileWithoutAccountBinding
import com.example.vitsi.utils.SystemBarColors
import com.example.vitsi.utils.ViewUtils


class ProfileWithoutAccountFragment : Fragment(R.layout.fragment_profile_without_account) {

    lateinit var binding: FragmentProfileWithoutAccountBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileWithoutAccountBinding.bind(requireView())
        binding.signUpBtn.setOnClickListener { findNavController().navigate(R.id.action_meFragment_to_signUpFragment) }
    }

    override fun onResume() {
        super.onResume()
        ViewUtils.changeSystemBars(activity, SystemBarColors.WHITE)
    }
}