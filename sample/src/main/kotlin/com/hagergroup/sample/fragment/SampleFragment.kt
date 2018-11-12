package com.hagergroup.sample.fragment

import com.hagergroup.sample.app.SampleFragmentAggregate
import com.hagergroup.sweetpotato.annotation.SweetLoadingAndErrorAnnotation
import com.hagergroup.sweetpotato.annotation.SweetSendLoadingIntentAnnotation
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetLoadingAndErrorAnnotation
@SweetSendLoadingIntentAnnotation
abstract class SampleFragment
  : SweetFragment<SampleFragmentAggregate>(),
    SweetFragmentAggregate.OnBackPressedListener
{

  override fun onBackPressed(): Boolean =
      false

}
