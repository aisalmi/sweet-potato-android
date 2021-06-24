package com.hagergroup.sweetpotato.app

import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import com.hagergroup.sweetpotato.content.LocalSharedFlowManager
import com.hagergroup.sweetpotato.content.SweetSharedFlowListener
import com.hagergroup.sweetpotato.content.SweetSharedFlowListenerProvider
import com.hagergroup.sweetpotato.content.SweetSharedFlowListenersProvider
import com.hagergroup.sweetpotato.lifecycle.SweetLifeCycle
import timber.log.Timber

/**
 * There for gathering all instance variables, and in order to make copy and paste smarter.
 *
 * @param AggregateClass the aggregate class accessible though the [Sweetened.setAggregate] and [Sweetened.getAggregate] methods
 * @param ComponentClass the instance the container has been created for
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class SweetStateContainer<AggregateClass : Any, ComponentClass : Any>(private val activity: AppCompatActivity, private val component: ComponentClass)
{

  private class RefreshModelAndBind(val retrieveModel: Boolean, val onOver: Runnable?)
  {

    fun refreshModelAndBind(lifeCycleActivity: SweetLifeCycle)
    {
      lifeCycleActivity.refreshModelAndBind(retrieveModel, onOver, true)
    }

  }

  companion object
  {

    private const val ALREADY_STARTED_EXTRA = "alreadyStartedExtra"

    private const val ILLEGAL_STATE_EXCEPTION_FRAGMENT_MESSAGE_SUFFIX = "not attached to Activity"

    fun isFirstCycle(savedInstanceState: Bundle?): Boolean =
        savedInstanceState?.containsKey(SweetStateContainer.ALREADY_STARTED_EXTRA) ?: false

  }

  var aggregate: AggregateClass? = null

  var firstLifeCycle = true

  var modelRetrieved = false
    private set

  var isInteracting = false
    private set

  var isAlive = true
    private set

  var resumedForTheFirstTime = true
    private set

  private var refreshingModelAndBindingCount = 0

  private var beingRedirected = false

  private var stopHandling = false

  private var refreshModelAndBindNextTime: RefreshModelAndBind? = null

  private var refreshModelAndBindPending: RefreshModelAndBind? = null

  fun isAliveAsWellAsHostingActivity(): Boolean =
      isAlive == true && activity.isFinishing == false

  fun onResume()
  {
    isInteracting = true
  }

  fun isRetrievingModel(): Boolean =
      modelRetrieved == false || refreshModelAndBindNextTime?.retrieveModel == true

  fun getRetrieveModelOver(): Runnable? =
      refreshModelAndBindNextTime?.onOver

  fun onRefreshingModelAndBindingStart()
  {
    refreshingModelAndBindingCount++
  }

  @Synchronized
  fun onRefreshingModelAndBindingStop(lifeCycleActivity: SweetLifeCycle)
  {
    refreshingModelAndBindingCount--

    // If the entity or the hosting Activity is not alive, we do nothing more
    if (isAliveAsWellAsHostingActivity() == false)
    {
      return
    }

    if (refreshModelAndBindPending != null)
    {
      Timber.d("The stacked refresh of the business objects and display is stacked can now be executed")

      refreshModelAndBindPending?.refreshModelAndBind(lifeCycleActivity)
      refreshModelAndBindPending = null
    }
  }

  fun isRefreshingModelAndBinding(): Boolean
  {
    return refreshingModelAndBindingCount > 0
  }

  fun onPause()
  {
    isInteracting = false
  }

  fun onDestroy()
  {
    isAlive = false
  }

  fun onSaveInstanceState(outState: Bundle)
  {
    outState.putBoolean(SweetStateContainer.ALREADY_STARTED_EXTRA, true)
  }

  fun beingRedirected()
  {
    beingRedirected = true
  }

  fun shouldKeepOn(): Boolean =
      stopHandling == false && beingRedirected == false

  @Synchronized
  fun shouldDelayRefreshModelAndBind(retrieveModel: Boolean, onOver: Runnable?, immediately: Boolean?): Boolean
  {
    // If the entity or the hosting Activity is finishing, we give up
    if (isAliveAsWellAsHostingActivity() == false)
    {
      return true
    }

    // We test whether the Activity is active (its life-cycle state is between 'onResume()' and 'onPause()'
    if (isInteracting == false && immediately == false)
    {
      refreshModelAndBindNextTime = RefreshModelAndBind(retrieveModel, onOver)

      Timber.d("The refresh of the business objects and display is delayed because the Activity is not interacting")

      return true
    }
    // We test whether the Activity is already being refreshed
    if (isRefreshingModelAndBinding() == true)
    {
      // In that case, we need to wait for the refresh action to be over
      Timber.d("The refresh of the model and bind is stacked because it is already refreshing")

      refreshModelAndBindPending = RefreshModelAndBind(retrieveModel, onOver)

      return true
    }

    refreshModelAndBindNextTime = null

    return false
  }

  fun onInternalModelAvailableExceptionWorkAround(throwable: Throwable): Boolean
  {
    if (throwable is IllegalStateException)
    {
      if (throwable.message?.endsWith(SweetStateContainer.ILLEGAL_STATE_EXCEPTION_FRAGMENT_MESSAGE_SUFFIX) == true)
      {
        return true
      }
    }

    return false
  }

  fun modelRetrieved()
  {
    modelRetrieved = true
  }

  fun stopHandling()
  {
    stopHandling = true
  }

  fun markNotResumedForTheFirstTime()
  {
    resumedForTheFirstTime = false
  }

  fun registerSweetSharedFlowListener(coroutineScope: LifecycleCoroutineScope, sweetSharedFlowListener: SweetSharedFlowListener)
  {
    coroutineScope.launchWhenCreated {
      LocalSharedFlowManager.collect(sweetSharedFlowListener)
    }
  }

  fun registerSweetSharedFlowListeners(coroutineScope: LifecycleCoroutineScope)
  {
    if (component is SweetSharedFlowListenersProvider)
    {
      Timber.d("Found out that the entity supports $component intent shared flow listeners")

      for (index in 0 until component.getSharedFlowListenersCount())
      {
        registerSweetSharedFlowListener(coroutineScope, component.getSharedFlowListener(index))
      }
    }
    else if (component is SweetSharedFlowListenerProvider)
    {
      Timber.d("Found out that the entity supports $component intent shared flow listener")

      registerSweetSharedFlowListener(coroutineScope, component.getSweetSharedFlowListener())
    }
    else if(component is SweetSharedFlowListener)
    {
      Timber.d("Found out that the entity supports $component a single intent shared flow listener")

      registerSweetSharedFlowListener(coroutineScope, component)
    }
  }

}
