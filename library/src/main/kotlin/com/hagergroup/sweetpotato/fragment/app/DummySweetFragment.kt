package com.hagergroup.sweetpotato.fragment.app

import androidx.annotation.RestrictTo
import androidx.databinding.ViewDataBinding
import com.hagergroup.sweetpotato.lifecycle.DummySweetViewModel

/**
 * An internal default [SweetFragment] class in order to provide a default value to the [com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation] annotation.
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class DummySweetFragment
  : SweetFragment<DummySweetFragmentAggregate, ViewDataBinding, DummySweetViewModel>()
{

  override fun getBindingVariable(): Int? =
      null

  override fun getViewModelClass(): Class<DummySweetViewModel> =
      DummySweetViewModel::class.java

}
