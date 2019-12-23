package com.hagergroup.sweetpotato.app

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.hagergroup.sweetpotato.content.SweetBroadcastListener
import timber.log.Timber

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
class Sweetizer<AggregateClass : Any, ComponentClass : Any>(val activity: AppCompatActivity,
                                                            val sweetable: Sweetable<AggregateClass>,
                                                            val component: ComponentClass,
                                                            val fragment: Fragment?)
  : Sweetable<AggregateClass>
{

  init
  {
    Timber.d("Creating the Sweetizer for Activity belonging to class '${activity.javaClass.name}' and with Fragment belonging to class '${fragment?.javaClass?.name}'")
  }

  private val stateContainer by lazy { StateContainer<AggregateClass, ComponentClass>(activity, component) }

  override fun getAggregate(): AggregateClass? =
      stateContainer.aggregate

  override fun setAggregate(aggregate: AggregateClass?)
  {
    stateContainer.aggregate = aggregate
  }

  override fun onException(throwable: Throwable, fromGuiThread: Boolean)
  {
    SweetActivityController.handleException(true, activity, fragment, throwable)
  }

  override fun registerBroadcastListeners(broadcastListeners: Array<SweetBroadcastListener>)
  {
    stateContainer.registerBroadcastListeners(broadcastListeners)
  }

  fun onCreate(superMethod: Runnable)
  {
    Timber.d("Sweetizer::onCreate")

    superMethod.run()

    if (isFragment() == false && SweetActivityController.needsRedirection(activity) == true)
    {
      // We stop here if a redirection is needed
      stateContainer.beingRedirected()

      return
    }
    else
    {
      SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_CREATE)
    }

    stateContainer.registerBroadcastListeners()
  }

  fun onNewIntent()
  {
    Timber.d("Sweetizer::onNewIntent")

    if (isFragment() == false && SweetActivityController.needsRedirection(activity) == true)
    {
      // We stop here if a redirection is needed
      stateContainer.beingRedirected()
    }
  }

  fun onResume()
  {
    Timber.d("Sweetizer::onResume")

    SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_RESUME)
  }

  fun onStart()
  {
    Timber.d("Sweetizer::onStart")

    SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_START)
  }

  fun onPause()
  {
    Timber.d("Sweetizer::onPause")

    SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_PAUSE)
  }

  fun onStop()
  {
    Timber.d("Sweetizer::onStop")

    SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_STOP)
  }

  fun onDestroy()
  {
    Timber.d("Sweetizer::onDestroy")

    stateContainer.onDestroy()

    SweetActivityController.onLifeCycleEvent(activity, fragment, Lifecycle.Event.ON_DESTROY)
  }

  private fun isFragment(): Boolean =
      activity != sweetable

}