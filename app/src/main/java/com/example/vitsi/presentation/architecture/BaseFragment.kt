package com.example.vitsi.presentation.architecture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vitsi.utils.ResUtils

abstract class BaseFragment(@LayoutRes val resId: Int?): Fragment() {

    open val viewModel by viewModels<BaseViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return resId?.let {
            inflater.inflate(resId, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLayout()
        setUpClickListeners()
        setUpLiveData()
    }

    abstract fun setUpLayout()

    open fun setUpClickListeners() {}

    open fun setUpLiveData() {
        viewModel.snackBarMessageRes.observe(viewLifecycleOwner) { resId ->
            resId?.let {
                ResUtils.showSnackBar(requireView(), resId)
                viewModel.clearMessage()
            }
        }
    }
}