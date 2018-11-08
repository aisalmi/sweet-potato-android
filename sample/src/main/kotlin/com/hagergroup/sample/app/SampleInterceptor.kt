package com.hagergroup.sample.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.app.SweetActivityInterceptor

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class SampleInterceptor
  : SweetActivityInterceptor<SampleActivityAggregate, SampleFragmentAggregate>()
{

  override fun instantiateActivityAggregate(activity: FragmentActivity, activityAnnotation: SweetActivityAnnotation?): SampleActivityAggregate =
      SampleActivityAggregate(activity, activityAnnotation)

  override fun instantiateFragmentAggregate(fragment: Fragment, fragmentAnnotation: SweetFragmentAnnotation?): SampleFragmentAggregate =
      SampleFragmentAggregate(fragment, fragmentAnnotation)

}
