package com.hagergroup.sweetpotato.app

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.hagergroup.sweetpotato.annotation.SweetActionBarAnnotation
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityAggregate
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate

/**
 * An interceptor which is responsible for handling the [SweetActionBarAnnotation], [SweetActivityAnnotation] and [SweetFragmentAnnotation] annotations declarations on [AppCompatActivity] and [Fragment].
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetActivityInterceptor<ActivityAggregateClass : SweetActivityAggregate, FragmentAggregateClass : SweetFragmentAggregate>
  : SweetActivityController.Interceptor
{

  protected abstract fun instantiateActivityAggregate(activity: AppCompatActivity, activityAnnotation: SweetActivityAnnotation?, actionBarAnnotation: SweetActionBarAnnotation?): ActivityAggregateClass

  protected abstract fun instantiateFragmentAggregate(fragment: Fragment, fragmentAnnotation: Any?): FragmentAggregateClass

  override fun onLifeCycleEvent(activity: AppCompatActivity, fragment: Fragment?, event: Lifecycle.Event)
  {
    if (event == Lifecycle.Event.ON_CREATE)
    {
      if (fragment is Sweetable<*>)
      {
        // It's a Fragment
        (fragment as Sweetable<FragmentAggregateClass>).apply {
          val sweetFragmentAnnotation = this::class.java.getAnnotation(SweetFragmentAnnotation::class.java)

          this.setAggregate(instantiateFragmentAggregate(fragment, sweetFragmentAnnotation))
          this.getAggregate()?.onCreate(activity)
        }
      }
      else
      {
        // It's an Activity
        (activity as Sweetable<ActivityAggregateClass>).apply {
          val sweetActivityAnnotation = this::class.java.getAnnotation(SweetActivityAnnotation::class.java)
          val sweetActionBarAnnotation = this::class.java.getAnnotation(SweetActionBarAnnotation::class.java)

          this.setAggregate(instantiateActivityAggregate(activity, sweetActivityAnnotation, sweetActionBarAnnotation))
          this.getAggregate()?.onCreate()
        }
      }
    }
  }

}