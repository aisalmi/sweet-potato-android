package com.hagergroup.sample.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.hagergroup.sample.app.SampleFragmentAggregate
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentConfigurable
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
abstract class SampleFragment<BindingClass : ViewDataBinding, ViewModelClass : SweetViewModel>
  : SweetFragment<SampleFragmentAggregate, BindingClass, ViewModelClass>(),
    SweetFragmentAggregate.OnBackPressedListener, SweetFragmentConfigurable
{

  abstract fun getRetryView(): View?

  override fun onViewCreated(view: View, savedInstanceState: Bundle?)
  {
    super.onViewCreated(view, savedInstanceState)

    getRetryView()?.setOnClickListener {
      viewModel?.refreshViewModel(true, null)
    }
  }

  override fun getBindingVariable(): Int =
      com.hagergroup.sample.BR.model

  override fun onBackPressed(): Boolean =
      false

}
