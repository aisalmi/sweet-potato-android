package com.hagergroup.sweetpotato.lifecycle

import androidx.lifecycle.ViewModel

/**
 * The basis class for all [ViewModel] available in the framework.
 *
 * @author Ludovic Roland
 * @since 2018.12.05
 */
abstract class SweetViewModel
  : ViewModel()
{

  var isAlreadyInitialized = false
    internal set

}