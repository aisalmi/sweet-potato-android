package com.hagergroup.sample.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel

/**
 * @author Ludovic Roland
 * @since 2018.11.09
 */
class SecondFragmentViewModel(application: Application)
  : SweetViewModel(application)
{

  var myString: String? = null

  val anotherString = ObservableField<String>()

}