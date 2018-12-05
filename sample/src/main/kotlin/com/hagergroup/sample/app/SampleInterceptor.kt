package com.hagergroup.sample.app

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hagergroup.sweetpotato.annotation.SweetActionBarAnnotation
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation
import com.hagergroup.sweetpotato.app.SweetActivityInterceptor

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class SampleInterceptor
  : SweetActivityInterceptor<SampleActivityAggregate, SampleFragmentAggregate>()
{

  override fun instantiateActivityAggregate(activity: AppCompatActivity, activityAnnotation: SweetActivityAnnotation?, actionBarAnnotation: SweetActionBarAnnotation?): SampleActivityAggregate =
      SampleActivityAggregate(activity, activityAnnotation, actionBarAnnotation)

  override fun instantiateFragmentAggregate(fragment: Fragment, fragmentAnnotation: Any?): SampleFragmentAggregate =
      SampleFragmentAggregate(fragment, fragmentAnnotation)

}
