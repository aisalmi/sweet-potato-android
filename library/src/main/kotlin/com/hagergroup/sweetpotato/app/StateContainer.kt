package com.hagergroup.sweetpotato.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hagergroup.sweetpotato.annotation.SweetSendLoadingIntentAnnotation
import com.hagergroup.sweetpotato.content.LoadingBroadcastListener
import com.hagergroup.sweetpotato.content.SweetBroadcastListener
import com.hagergroup.sweetpotato.content.SweetBroadcastListenerProvider
import com.hagergroup.sweetpotato.content.SweetBroadcastListenersProvider
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
internal class StateContainer<AggregateClass : Any, ComponentClass : Any>(private val activity: AppCompatActivity, private val component: ComponentClass)
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
        savedInstanceState?.containsKey(StateContainer.ALREADY_STARTED_EXTRA) ?: false

  }

  val handler by lazy { Handler() }

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

  var backFromBackStack = false
    private set

  private var refreshingModelAndBindingCount = 0

  private var beingRedirected = false

  private var stopHandling = false

  private var broadcastReceivers: Array<BroadcastReceiver?>? = null

  private var refreshModelAndBindNextTime: RefreshModelAndBind? = null

  private var refreshModelAndBindPending: RefreshModelAndBind? = null

  private fun registerBroadcastListeners(index: Int, broadcastListener: SweetBroadcastListener)
  {
    if (index == 0)
    {
      Timber.d("Registering for listening to intent broadcasts")
    }

    val broadcastReceiver = object : BroadcastReceiver()
    {

      override fun onReceive(context: Context?, intent: Intent?)
      {
        try
        {
          broadcastListener.onReceive(context, intent)
        }
        catch (throwable: Throwable)
        {
          Timber.e(throwable, "An exception occurred while handling a broadcast intent!")
        }
      }

    }

    broadcastReceivers?.set(index, broadcastReceiver)
    LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiver, broadcastListener.getIntentFilter())
  }

  private fun enrichBroadcastListeners(count: Int): Int
  {
    val newIndex: Int

    if (broadcastReceivers == null)
    {
      newIndex = 0
      broadcastReceivers = arrayOfNulls(count)
    }
    else
    {
      newIndex = broadcastReceivers?.size ?: 0

      val newBroadcastReceivers = arrayOfNulls<BroadcastReceiver>(count + (broadcastReceivers?.size ?: 0))

      broadcastReceivers?.indices?.forEach {
        newBroadcastReceivers[it] = broadcastReceivers?.get(it)
      }

      broadcastReceivers = newBroadcastReceivers
    }

    Timber.d("The entity is now able to welcome ${broadcastReceivers?.size} broadcast receiver(s)")

    return newIndex
  }

  fun isAliveAsWellAsHostingActivity(): Boolean =
      isAlive == true && activity.isFinishing == false

  fun registerBroadcastListeners()
  {
    if (component is SweetBroadcastListenersProvider)
    {
      val count = component.getBroadcastListenersCount()

      Timber.d("Found out that the entity supports $count intent broadcast listeners")

      val startIndex = enrichBroadcastListeners(count)

      for (index in 0 until count)
      {
        registerBroadcastListeners(startIndex + index, component.getBroadcastListener(index))
      }
    }
    else if (component is SweetBroadcastListenerProvider)
    {
      Timber.d("Found out that the entity supports a single intent broadcast listener")

      registerBroadcastListeners(enrichBroadcastListeners(1), component.getBroadcastListener())
    }
    else if (component is SweetBroadcastListener)
    {
      Timber.d("Found out that the entity implements a single intent broadcast listener")

      registerBroadcastListeners(enrichBroadcastListeners(1), component)
    }
  }

  fun registerBroadcastListeners(broadcastListeners: Array<SweetBroadcastListener>)
  {
    val startIndex = enrichBroadcastListeners(broadcastListeners.size)

    broadcastListeners.indices.forEach {
      registerBroadcastListeners(it + startIndex, broadcastListeners[it])
    }
  }

  fun unregisterBroadcastListeners()
  {
    broadcastReceivers?.let { broadcastReceiver ->
      broadcastReceiver.indices.reversed().forEach { indice ->
        broadcastReceiver[indice]?.let { currentBroadcastReceiver ->
          LocalBroadcastManager.getInstance(activity).unregisterReceiver(currentBroadcastReceiver)
        }
      }

      Timber.d("Stopped listening to ${broadcastReceivers?.size} intent broadcasts")
    }
  }

  fun onStartLoading()
  {
    if (component::class.java.getAnnotation(SweetSendLoadingIntentAnnotation::class.java) != null)
    {
      // We indicate the activity which is loading, in order to filter the loading events
      LoadingBroadcastListener.broadcastLoading(activity, System.identityHashCode(activity), System.identityHashCode(component), true)
    }
  }

  fun onStopLoading()
  {
    if (component::class.java.getAnnotation(SweetSendLoadingIntentAnnotation::class.java) != null)
    {
      // We indicate the activity which is loading, in order to filter the loading events
      LoadingBroadcastListener.broadcastLoading(activity, System.identityHashCode(activity), System.identityHashCode(component), false)
    }
  }

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

  fun onDestroyView()
  {
    backFromBackStack = true
  }

  fun onDestroy()
  {
    backFromBackStack = false
    isAlive = false

    // If the business objects retrieval and synchronization is not yet completed, we do not forget to notify
    if (isRefreshingModelAndBinding() == true)
    {
      onStopLoading()
    }

    // We unregister all the "BroadcastListener" entities
    unregisterBroadcastListeners()
  }

  fun onSaveInstanceState(outState: Bundle)
  {
    outState.putBoolean(StateContainer.ALREADY_STARTED_EXTRA, true)
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
      if (throwable.message?.endsWith(StateContainer.ILLEGAL_STATE_EXCEPTION_FRAGMENT_MESSAGE_SUFFIX) == true)
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

}