package com.jonnydev.bmp.extension

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

val Int.seconds: Int
    get() = this / 1000

val Long.seconds: Int
    get() = (this / 1000).toInt()

val Int.milliseconds: Int
    get() = this * 1000

fun Int.toTimeFormat(): String {
    val second = seconds % 60
    val minute = this / (1000 * 60) % 60
    val hour = this / (1000 * 60 * 60)
    
    if (hour > 0)
        return "%d:%02d:%02d".format(hour, minute, second)
    
    return "%02d:%02d".format(minute, second)
}

fun Long.toTimeFormat(): String {
    val second = this / 1000 % 60
    val minute = this / (1000 * 60) % 60
    val hour = this / (1000 * 60 * 60)
    
    if (hour > 0)
        return "%d:%02d:%02d".format(hour, minute, second)
    
    return "%02d:%02d".format(minute, second)
}

fun Any.inf(message: String) {
    Log.i(this::class.java.simpleName, message)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER_VERTICAL, 0, 0)
    }.show()
