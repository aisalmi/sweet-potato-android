package com.hagergroup.sweetpotato.databinding

import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter

/**
 * @author Ludovic Roland
 * @since 2019.12.24
 */
@BindingAdapter("animatedVisibility")
fun View.setAnimatedVisibility(value: Int?)
{
  value?.let {
    if (this.visibility != it)
    {
      TransitionManager.beginDelayedTransition(this.rootView as ViewGroup, Fade())
      this.visibility = it
    }
  }
}