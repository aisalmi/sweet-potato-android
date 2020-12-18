package com.hagergroup.sample.app

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hagergroup.sweetpotato.app.SweetActivityInterceptor
import com.hagergroup.sweetpotato.appcompat.app.SweetActionBarConfigurable
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityConfigurable
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentConfigurable

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
class SampleInterceptor
  : SweetActivityInterceptor<SampleActivityAggregate, SampleFragmentAggregate>()
{

  override fun instantiateActivityAggregate(activity: AppCompatActivity, activityConfigurable: SweetActivityConfigurable?, actionBarConfigurable: SweetActionBarConfigurable?): SampleActivityAggregate =
      SampleActivityAggregate(activity, activityConfigurable, actionBarConfigurable)

  override fun instantiateFragmentAggregate(fragment: Fragment, fragmentConfigurable: SweetFragmentConfigurable?): SampleFragmentAggregate =
      SampleFragmentAggregate(fragment, fragmentConfigurable)

}
