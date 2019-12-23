package com.hagergroup.sample.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.hagergroup.sample.R
import com.hagergroup.sample.fragment.ThirdFragment
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel
import kotlinx.coroutines.delay
import java.net.UnknownHostException

/**
 * @author Ludovic Roland
 * @since 2019.03.21
 */
class ThirdFragmentViewModel(application: Application)
  : SweetViewModel(application)
{

  var throwError = false

  var throwInternetError = false

  val persons = mutableListOf<String>()

  var myString: String? = null

  val anotherString = MutableLiveData<String>()

  val resString = MutableLiveData<@StringRes Int>().apply {
    postValue(R.string.app_name)
  }

  override suspend fun computeViewModel(arguments: Bundle?)
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

    myString = arguments?.getString(ThirdFragment.MY_EXTRA)
    anotherString.postValue(arguments?.getString(ThirdFragment.ANOTHER_EXTRA))
    persons.addAll(Array(15) { "Person ${it + 1}" })
  }

}