package com.example.whyCats.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

const val NOT_AVAILABLE = "Not available"

fun ImageView.loadImage(imageUrl: String) {
    Glide.with(this).load(imageUrl).into(this)
}

fun TextView.showElement(text: String?) {
    this.isVisible = !text.isNullOrEmpty()
}

fun showSnackBarMessage(root: View, message: String) {
    Snackbar.make(root, message, Snackbar.LENGTH_LONG).show()
}

fun getDateTimeStamp(): String {
    return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
}