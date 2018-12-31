package com.hagergroup.sweetpotato.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.hagergroup.sweetpotato.annotation.SweetActionBarAnnotation
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.annotation.SweetViewModelBindingFragmentAnnotation
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

  private val fragmentLoadingErrorAndRetryAggregate by lazy { SweetLoadingAndErrorInterceptor.LoadingErrorAndRetryAggregate() }

  private val fragmentModelUnavailableExceptionKeeper by lazy { SweetLoadingAndErrorInterceptor.ModelUnavailableExceptionKeeper() }

  val modelContainer by lazy { SweetActivityInterceptor.ModelContainer() }

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
          val sweetFragmentAnnotation = this::class.java.getAnnotation(SweetFragmentAnnotation::class.java) ?: this::class.java.getAnnotation(SweetViewModelBindingFragmentAnnotation::class.java)

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