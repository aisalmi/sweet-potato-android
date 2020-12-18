package com.hagergroup.sweetpotato.lifecycle

import android.app.Application
import androidx.lifecycle.SavedStateHandle

/**
 * A default SweetViewModel implementation in order to provide a default value to the
 * [com.hagergroup.sweetpotato.fragment.app.SweetFragmentConfigurable] interface
 *
 * @author Ludovic Roland
 * @since 2018.12.05
 */
class DummySweetViewModel(application: Application, savedStateHandle: SavedStateHandle)
  : SweetViewModel(application, savedStateHandle)
{

  override suspend fun computeViewModel()
  {

  }

}