package com.hagergroup.sweetpotato.lifecycle

import android.app.Application

/**
 * A default SweetViewModel implementation in order to provide a default value to the
 * [com.hagergroup.sweetpotato.annotation.SweetViewModelBindingFragmentAnnotation] annotation
 *
 * @author Ludovic Roland
 * @since 2018.12.05
 */
class DummySweetViewModel(application: Application)
  : SweetViewModel(application)