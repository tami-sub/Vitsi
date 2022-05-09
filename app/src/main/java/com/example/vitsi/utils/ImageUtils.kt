package com.example.vitsi.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.progressindicator.CircularProgressIndicator
import timber.log.Timber

object ImageUtils {

    fun loadGlideImage(imageView: ImageView, profilePictureUrl: String?) {
        Glide.with(imageView)
            .load(profilePictureUrl)
            .into(imageView)
    }

    fun loadGlideImage(imageView: ImageView, @DrawableRes drawableRes: Int) {
        Glide.with(imageView)
            .load(drawableRes)
            .into(imageView)
    }

    fun loadGlideImage(imageView: ImageView, bitmap: Bitmap) {
        Glide.with(imageView)
            .load(bitmap)
            .into(imageView)
    }

    inline fun <T> getRequestListener(progressBar: CircularProgressIndicator, crossinline onLoadFailed: () -> Unit) =
        object : RequestListener<T> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<T>?,
                isFirstResource: Boolean
            ): Boolean {
                Timber.e(e)
                onLoadFailed()
                return false
            }

            override fun onResourceReady(
                resource: T,
                model: Any?,
                target: Target<T>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar.visibility = View.GONE
                return false
            }
        }
}