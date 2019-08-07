package com.hagergroup.sample.viewmodel

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.hagergroup.sample.R
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel

/**
 * @author Ludovic Roland
 * @since 2019.03.21
 */
class ThirdFragmentViewModel(application: Application)
  : SweetViewModel(application)
{

  val persons = mutableListOf<String>()

  var myString: String? = null

  val anotherString = MutableLiveData<String>()

  val resString = MutableLiveData<@StringRes Int>().apply {
    postValue(R.string.app_name)
  }

}