package com.hagergroup.sweetpotato.fragment.app

import androidx.annotation.RestrictTo
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation

/**
 * An internal default [SweetFragment] class in order to provide a default value to the [com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation] annotation.
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@SweetFragmentAnnotation(layoutId = android.R.layout.list_content)
internal class DummySweetFragment
  : SweetFragment<DummySweetFragmentAggregate>()
{

  override fun onBindModel()
  {
  }

}