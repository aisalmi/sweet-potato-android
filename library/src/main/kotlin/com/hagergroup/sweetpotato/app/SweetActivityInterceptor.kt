package com.hagergroup.sweetpotato.app

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.hagergroup.sweetpotato.appcompat.app.SweetActionBarConfigurable
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityAggregate
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityConfigurable
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentConfigurable

/**
 * An interceptor which is responsible for handling the [SweetActionBarConfigurable], [SweetActivityConfigurable] and [SweetFragmentConfigurable] interfaces declarations on [AppCompatActivity] and [Fragment].
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetActivityInterceptor<ActivityAggregateClass : SweetActivityAggregate, FragmentAggregateClass : SweetFragmentAggregate>
  : SweetActivityController.Interceptor
{

  protected abstract fun instantiateActivityAggregate(activity: AppCompatActivity, activityConfigurable: SweetActivityConfigurable?, actionBarConfigurable: SweetActionBarConfigurable?): ActivityAggregateClass

  protected abstract fun instantiateFragmentAggregate(fragment: Fragment, fragmentConfigurable: SweetFragmentConfigurable?): FragmentAggregateClass

  override fun onLifeCycleEvent(activity: AppCompatActivity, fragment: Fragment?, event: Lifecycle.Event)
  {
    if (event == Lifecycle.Event.ON_CREATE)
    {
      if (fragment is Sweetable<*>)
      {
        // It's a Fragment
        (fragment as? Sweetable<FragmentAggregateClass>)?.apply {
          this.setAggregate(instantiateFragmentAggregate(fragment, (fragment as? SweetFragmentConfigurable)))
          this.getAggregate()?.onCreate(activity)
        }
      }
      else
      {
        // It's an Activity
        (activity as Sweetable<ActivityAggregateClass>).apply {
          this.setAggregate(instantiateActivityAggregate(activity, (activity as? SweetActivityConfigurable), (activity as? SweetActionBarConfigurable)))
          this.getAggregate()?.onCreate()
        }
      }
    }
  }

}