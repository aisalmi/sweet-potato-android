package com.hagergroup.sweetpotato.lifecycle

import androidx.lifecycle.ViewModel

/**
 * @author Ludovic Roland
 * @since 2018.12.05
 */
abstract class SweetViewModel
  : ViewModel()
{

  var isAlreadyInitialized = false

}