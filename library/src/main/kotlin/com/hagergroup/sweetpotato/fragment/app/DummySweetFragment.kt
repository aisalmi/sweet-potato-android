package com.hagergroup.sweetpotato.fragment.app

import androidx.annotation.RestrictTo

/**
 * An internal default [SweetFragment] class in order to provide a default value to the [com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation] annotation.
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class DummySweetFragment
  : SweetFragment<DummySweetFragmentAggregate>()
{

  override fun onBindModel()
  {
  }

}