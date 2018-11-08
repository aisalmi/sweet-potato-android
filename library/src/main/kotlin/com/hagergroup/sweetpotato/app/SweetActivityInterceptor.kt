package com.hagergroup.sweetpotato.app

import android.os.Bundle
import androidx.fragment.app.Fragment
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
abstract class SweetActivityInterceptor<ActivityAggregateClass : SweetActivityAggregate, FragmentAggregateClass : SweetFragmentAggregate>
  : SweetActivityController.Interceptor
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

  protected abstract fun instantiateActivityAggregate(activity: FragmentActivity, activityAnnotation: SweetActivityAnnotation?): ActivityAggregateClass

  protected abstract fun instantiateFragmentAggregate(fragment: Fragment, fragmentAnnotation: SweetFragmentAnnotation?): FragmentAggregateClass

  override fun onLifeCycleEvent(activity: FragmentActivity?, fragment: Fragment?, event: Lifecycle.Event)
  {
    if (event == Lifecycle.Event.ON_CREATE)
    {
      if (fragment is Sweetable<*>)
      {
        // It's a Fragment
        (fragment as Sweetable<FragmentAggregateClass>).let {
          (it::class.findAnnotation<SweetFragmentAnnotation>())?.let { annotation ->
            it.setAggregate(instantiateFragmentAggregate(fragment, annotation))
            it.getAggregate()?.onCreate(activity)
          }
        }
      }
      else
      {
        // It's an Activity
        (activity as? Sweetable<ActivityAggregateClass>)?.let {
          (it::class.findAnnotation<SweetActivityAnnotation>())?.let { annotation ->
            it.setAggregate(instantiateActivityAggregate(activity, annotation))
            it.getAggregate()?.onCreate()
          }
        }
      }
    }
  }

}