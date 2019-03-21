package com.hagergroup.sample.viewmodel

import android.app.Application
import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.hagergroup.sample.R
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel

/**
 * @author Ludovic Roland
 * @since 2019.03.21
 */
class ThirdFragmentViewModel(application: Application)
  : SweetViewModel(application)
{

  var myString: String? = null

  val anotherString = ObservableField<String>()

  @StringRes
  val resString = ObservableInt(R.string.app_name)

}