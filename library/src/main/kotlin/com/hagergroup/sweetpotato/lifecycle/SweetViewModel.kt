package com.hagergroup.sweetpotato.lifecycle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

/**
 * The basis class for all [ViewModel] available in the framework.
 *
 * @author Ludovic Roland
 * @since 2018.12.05
 */
abstract class SweetViewModel(application: Application)
  : AndroidViewModel(application)
{

  var isAlreadyInitialized = false
    internal set

}