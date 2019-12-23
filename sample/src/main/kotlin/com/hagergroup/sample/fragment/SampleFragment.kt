package com.hagergroup.sample.fragment

import androidx.databinding.ViewDataBinding
import com.hagergroup.sample.app.SampleFragmentAggregate
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
abstract class SampleFragment<BindingClass : ViewDataBinding, ViewModelClass : SweetViewModel>
  : SweetFragment<SampleFragmentAggregate, BindingClass, ViewModelClass>(),
    SweetFragmentAggregate.OnBackPressedListener
{

  override fun getBindingVariable(): Int =
      com.hagergroup.sample.BR.model

  override fun onBackPressed(): Boolean =
      false

}
