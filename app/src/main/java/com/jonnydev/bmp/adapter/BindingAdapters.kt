package com.jonnydev.bmp.adapter

import android.graphics.drawable.Drawable
import android.widget.ImageButton
import androidx.databinding.BindingAdapter

@BindingAdapter("srcCompat")
fun bindSrcCompat(iBtn: ImageButton, drawable: Drawable) {
    iBtn.setImageDrawable(drawable)
}
