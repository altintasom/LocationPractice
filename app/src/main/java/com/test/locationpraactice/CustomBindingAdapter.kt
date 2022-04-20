package com.test.locationpraactice

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("android:customProgressVisibility")
fun setCustomProgressVisibility(view: View, isVisiable: Boolean){
    if (isVisiable)
        view.visibility = View.VISIBLE
    else
        view.visibility = View.GONE

}
@BindingAdapter("android:customLocationVisibility")
fun setCustomLocationVisibility(view: View, isVisiable: Boolean){
    if (isVisiable)
        view.visibility = View.GONE
    else
        view.visibility = View.VISIBLE

}