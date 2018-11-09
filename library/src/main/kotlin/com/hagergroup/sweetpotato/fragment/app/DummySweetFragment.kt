package com.hagergroup.sweetpotato.fragment.app

import androidx.annotation.RestrictTo

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class DummySweetFragment
  : SweetFragment<Void>()
{

  override fun onRetrieveModel()
  {
  }

  override fun onBindModel()
  {
  }

}