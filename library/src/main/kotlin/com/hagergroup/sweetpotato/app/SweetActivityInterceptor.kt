package com.hagergroup.sweetpotato.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.appcompat.app.SweetActivityAggregate
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetActivityInterceptor<ActivityAggregateClass : SweetActivityAggregate, FragmentAggregateClass : SweetFragmentAggregate>
  : SweetActivityController.Interceptor
{

  class ModelContainer
  {

    companion object
    {

      private const val MODEL_UNAVAILABLE_EXCEPTION_EXTRA = "modelUnavailableExceptionExtra"

    }

    private var exception: ModelUnavailableException? = null

    fun onRestoreInstanceState(bundle: Bundle?)
    {
      if (bundle != null)
      {
        exception = bundle.getSerializable(ModelContainer.MODEL_UNAVAILABLE_EXCEPTION_EXTRA) as? ModelUnavailableException
      }
    }

    fun onSaveInstanceState(bundle: Bundle)
    {
      if (exception != null)
      {
        bundle.putSerializable(ModelContainer.MODEL_UNAVAILABLE_EXCEPTION_EXTRA, exception)
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
          (it::class.java.getAnnotation(SweetFragmentAnnotation::class.java))?.let { annotation ->
            it.setAggregate(instantiateFragmentAggregate(fragment, annotation))
            it.getAggregate()?.onCreate(activity)
          }
        }
      }
      else
      {
        // It's an Activity
        (activity as? Sweetable<ActivityAggregateClass>)?.let {
          (it::class.java.getAnnotation(SweetActivityAnnotation::class.java))?.let { annotation ->
            it.setAggregate(instantiateActivityAggregate(activity, annotation))
            it.getAggregate()?.onCreate()
          }
        }
      }
    }
  }

}