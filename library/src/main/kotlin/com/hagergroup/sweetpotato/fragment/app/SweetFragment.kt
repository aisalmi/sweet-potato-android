package com.hagergroup.sweetpotato.fragment.app

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.app.Sweetizer
import com.hagergroup.sweetpotato.content.SweetBroadcastListener

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetFragment<AggregateClass : Any>
  : Fragment(),
    Sweetable<AggregateClass>
{

  private var sweetizer: Sweetizer<AggregateClass, SweetFragment<AggregateClass>>? = null

  @CallSuper
  override fun onAttach(context: Context?)
  {
    super.onAttach(context)

    activity?.let {
      sweetizer = Sweetizer(it, this, this, this)
    }
  }

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?)
  {
    sweetizer?.onCreate(Runnable {
      super@SweetFragment.onCreate(savedInstanceState)
    }, savedInstanceState)
  }

  @CallSuper
  override fun onStart()
  {
    super.onStart()
    sweetizer?.onStart()
  }

  @CallSuper
  override fun onResume()
  {
    super.onResume()
    sweetizer?.onResume()
  }

  @CallSuper
  override fun onPause()
  {
    try
    {
      sweetizer?.onPause()
    }
    finally
    {
      super.onPause()
    }
  }

  @CallSuper
  override fun onStop()
  {
    try
    {
      sweetizer?.onStop()
    }
    finally
    {
      super.onStop()
    }
  }

  @CallSuper
  override fun onDestroy()
  {
    try
    {
      sweetizer?.onDestroy()
    }
    finally
    {
      super.onDestroy()
    }
  }

  @CallSuper
  override fun onSaveInstanceState(outState: Bundle)
  {
    super.onSaveInstanceState(outState)
    sweetizer?.onSaveInstanceState(outState)
  }

  override fun getAggregate(): AggregateClass?
  {
    return sweetizer?.getAggregate()
  }

  override fun setAggregate(aggregate: AggregateClass)
  {
    sweetizer?.setAggregate(aggregate)
  }

  override fun getHandler(): Handler =
      sweetizer?.getHandler() ?: Handler()

  @CallSuper
  override fun onException(throwable: Throwable, fromGuiThread: Boolean)
  {
    sweetizer?.onException(throwable, fromGuiThread)
  }

  @CallSuper
  override fun registerBroadcastListeners(broadcastListeners: Array<SweetBroadcastListener>)
  {
    sweetizer?.registerBroadcastListeners(broadcastListeners)
  }

  override fun isRefreshingViewModelAndBinding(): Boolean =
      sweetizer?.isRefreshingViewModelAndBinding() ?: false

  override fun isFirstLifeCycle(): Boolean =
      sweetizer?.isFirstLifeCycle() ?: false

  override fun isInteracting(): Boolean =
      sweetizer?.isInteracting() ?: false

  override fun isAlive(): Boolean =
      sweetizer?.isAlive() ?: false

  override fun refreshViewModelAndBind(onOver: Runnable?)
  {
    sweetizer?.refreshViewModelAndBind(onOver)
  }

  override fun shouldKeepOn(): Boolean =
      sweetizer?.shouldKeepOn() ?: false

  final override fun onRetrieveDisplayObjects()
  {
  }

  fun refreshViewModelAndBind()
  {
    refreshViewModelAndBind(null)
  }

}