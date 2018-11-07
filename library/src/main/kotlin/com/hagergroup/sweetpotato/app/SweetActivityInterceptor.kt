package com.hagergroup.sweetpotato.app

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityAggregate
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.lifecycle.ViewModelUnavailableException
import kotlin.reflect.full.findAnnotation

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetActivityInterceptor<ActivityAggregateClass : SweetActivityAggregate<out SweetApplication>, FragmentAggregateClass : SweetFragmentAggregate<out SweetApplication>>
  : SweetActivityController.SweetInterceptor
{

  class ViewModelContainer
  {

    companion object
    {

      private const val VIEW_MODEL_UNAVAILABLE_EXCEPTION_EXTRA = "viewModelUnavailableExceptionExtra"

    }

    private var exception: ViewModelUnavailableException? = null

    fun onRestoreInstanceState(bundle: Bundle?)
    {
      if (bundle != null)
      {
        exception = bundle.getSerializable(ViewModelContainer.VIEW_MODEL_UNAVAILABLE_EXCEPTION_EXTRA) as? ViewModelUnavailableException
      }
    }

    fun onSaveInstanceState(bundle: Bundle)
    {
      if (exception != null)
      {
        bundle.putSerializable(ViewModelContainer.VIEW_MODEL_UNAVAILABLE_EXCEPTION_EXTRA, exception)
      }
    }

  }

  protected abstract fun instantiateActivityAggregate(activity: FragmentActivity, sweetable: Sweetable<ActivityAggregateClass>, annotation: SweetActivityAnnotation): ActivityAggregateClass

  protected abstract fun instantiateFragmentAggregate(sweetable: Sweetable<FragmentAggregateClass>, fragmentAnnotation: SweetFragmentAnnotation): FragmentAggregateClass

  override fun onLifeCycleEvent(activity: FragmentActivity?, component: Any?, event: Lifecycle.Event)
  {
    if (event == Lifecycle.Event.ON_CREATE)
    {
      if (component is Sweetable<*>)
      {
        // It's a Fragment
        (component as? Sweetable<FragmentAggregateClass>)?.let {
          val fragmentAnnotation = it::class.findAnnotation<SweetFragmentAnnotation>() ?: throw IllegalArgumentException("The fragment annotation is missing")
          it.setAggregate(instantiateFragmentAggregate(it, fragmentAnnotation))
          it.getAggregate()?.onCreate(activity)
        }
      }
      else
      {
        // It's an Activity
        (activity as? Sweetable<ActivityAggregateClass>)?.let {
          val activityAnnotation = it::class.findAnnotation<SweetActivityAnnotation>() ?: throw IllegalArgumentException("The activity annotation is missing")
          it.setAggregate(instantiateActivityAggregate(activity, it, activityAnnotation))
          it.getAggregate()?.onCreate()
        }
      }
    }
  }

}