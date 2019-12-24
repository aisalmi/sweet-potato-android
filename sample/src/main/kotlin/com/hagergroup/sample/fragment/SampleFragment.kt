package com.hagergroup.sample.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.hagergroup.sample.app.SampleFragmentAggregate
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel
import kotlinx.android.synthetic.main.loading_error_and_retry.*

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
abstract class SampleFragment<BindingClass : ViewDataBinding, ViewModelClass : SweetViewModel>
  : SweetFragment<SampleFragmentAggregate, BindingClass, ViewModelClass>(),
    SweetFragmentAggregate.OnBackPressedListener
{

  override fun onViewCreated(view: View, savedInstanceState: Bundle?)
  {
    super.onViewCreated(view, savedInstanceState)

    retry?.setOnClickListener {
      getCastedViewModel()?.refreshViewModel(arguments, true, null)
    }
  }

  override fun getBindingVariable(): Int =
      com.hagergroup.sample.BR.model

  override fun onBackPressed(): Boolean =
      false

}
