package com.example.vitsi.presentation.ui.components.video

import android.net.Uri
import android.view.View
import com.example.vitsi.R
import com.example.vitsi.databinding.SmallVideoLayoutBinding
import com.example.vitsi.models.video.RemoteVideo
import com.example.vitsi.utils.ImageUtils.getRequestListener
import com.example.vitsi.utils.TimeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xwray.groupie.viewbinding.BindableItem
import timber.log.Timber

class SmallVideoGroup(
    private val remoteVideo: RemoteVideo,
    private val onClickListener: () -> Unit,
    private val onLoadFailed: (SmallVideoGroup) -> Unit
) : BindableItem<SmallVideoLayoutBinding>() {

    override fun bind(binding: SmallVideoLayoutBinding, position: Int) {
        Timber.d("Cursor url is ${remoteVideo.url} and Uri parser uri is ${Uri.parse(remoteVideo.url)}")
        binding.root.setOnClickListener { onClickListener() }
        binding.smallVideoDuration.text =
            TimeUtils.convertTimeToDisplayTime(remoteVideo.duration)

        val requestOptions = RequestOptions()
        Glide
            .with(binding.root)
            .applyDefaultRequestOptions(requestOptions)
            .load(remoteVideo.url)
            .addListener(getRequestListener(binding.loadingBar) { onLoadFailed(this) })
            .into(binding.smallVideoThumbnail)
    }

    override fun initializeViewBinding(view: View) =
        SmallVideoLayoutBinding.bind(view)

    override fun getLayout(): Int = R.layout.small_video_layout
}