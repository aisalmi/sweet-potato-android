package com.hagergroup.sample.fragment

import androidx.databinding.ViewDataBinding
import com.hagergroup.sample.app.SampleFragmentAggregate
import com.hagergroup.sweetpotato.annotation.SweetLoadingAndErrorAnnotation
import com.hagergroup.sweetpotato.annotation.SweetSendLoadingIntentAnnotation
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.fragment.app.SweetViewModelBindingFragment

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetLoadingAndErrorAnnotation
@SweetSendLoadingIntentAnnotation
abstract class SampleViewModelBindingFragment<BindingClass : ViewDataBinding>
  : SweetViewModelBindingFragment<SampleFragmentAggregate, BindingClass>(),
    SweetFragmentAggregate.OnBackPressedListener
{

  override fun getBindingVariable(): Int =
      com.hagergroup.sample.BR.model

  override fun onBackPressed(): Boolean =
      false

}
