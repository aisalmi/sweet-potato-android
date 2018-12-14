package com.hagergroup.sample.viewmodel

import androidx.databinding.ObservableField
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel

/**
 * @author Ludovic Roland
 * @since 2018.11.09
 */
class SecondFragmentViewModel
  : SweetViewModel()
{

  var myString: String? = null

  val anotherString = ObservableField<String>()

}