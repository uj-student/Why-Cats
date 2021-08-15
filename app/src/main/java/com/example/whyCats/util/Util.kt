package com.example.whyCats.util

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide

const val NOT_AVAILABLE = "Not available"

fun ImageView.loadImage(imageUrl: String) {
    Glide.with(this).load(imageUrl).into(this)
}

fun TextView.showElement(text: String?) {
    this.isVisible = !text.isNullOrEmpty()
}