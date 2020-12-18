package com.hagergroup.sample.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.hagergroup.sample.fragment.SecondFragment
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel
import kotlinx.coroutines.delay
import java.net.UnknownHostException

/**
 * @author Ludovic Roland
 * @since 2018.11.09
 */
class SecondFragmentViewModel(application: Application, savedStateHandle: SavedStateHandle)
  : SweetViewModel(application, savedStateHandle)
{

  var myString: String? = null

  val anotherString = MutableLiveData<String>()

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

    myString = savedStateHandle.get<String?>(SecondFragment.MY_EXTRA)
    anotherString.postValue(savedStateHandle.get(SecondFragment.ANOTHER_EXTRA))
  }
}