package com.hagergroup.sample.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel
import kotlinx.coroutines.delay
import java.net.UnknownHostException

/**
 * @author Ludovic Roland
 * @since 2019.12.23
 */
class MainFragmentViewModel(application: Application, savedStateHandle: SavedStateHandle)
  : SweetViewModel(application, savedStateHandle)
{

  var throwError = false

  var throwInternetError = false

  override suspend fun computeViewModel()
  {
    delay(1_000)

    if (throwError == true)
    {
      throwError = false

      throw ModelUnavailableException("Cannot retrieve the model")
    }

    if (throwInternetError == true)
    {
      throwInternetError = false

      throw ModelUnavailableException("Cannot retrieve the model", UnknownHostException())
    }
  }

}