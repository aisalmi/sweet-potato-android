package com.hagergroup.sweetpotato.fragment.app

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.app.Sweetizer
import com.hagergroup.sweetpotato.content.SweetBroadcastListener
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException

/**
 * A basis class for designing an Android compatibility library [Fragment] compatible with the framework, i.e.
 * sweet potato ready.
 *
 * @param AggregateClass the aggregate class accessible though the [setAggregate] and [getAggregate] methods
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetFragment<AggregateClass : SweetFragmentAggregate>
  : Fragment(),
    Sweetable<AggregateClass>
{

  private var sweetizer: Sweetizer<AggregateClass, SweetFragment<AggregateClass>>? = null

  @CallSuper
  override fun onAttach(context: Context?)
  {
    super.onAttach(context)

    (activity as? AppCompatActivity)?.let {
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
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
  {
    savedInstanceState?.let {
      getAggregate()?.onRestoreInstanceState(it)
    }

    return inflateLayout(inflater, container)
  }

  protected open fun inflateLayout(inflater: LayoutInflater, container: ViewGroup?): View?
  {
    return if (getLayoutId() != -1)
    {
      inflater.inflate(getLayoutId(), container, false)
    }
    else
    {
      inflater.inflate(getAggregate()?.getFragmentLayoutIdFromAnnotation() ?: -1, container, false)
    }
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
  @Throws(ModelUnavailableException::class)
  override fun onRetrieveModel()
  {
    getAggregate()?.checkException()
  }

  override fun onBindModel()
  {
  }

  @CallSuper
  override fun onSaveInstanceState(outState: Bundle)
  {
    super.onSaveInstanceState(outState)
    sweetizer?.onSaveInstanceState(outState)
    getAggregate()?.onSaveInstanceState(outState)
  }

  override fun getAggregate(): AggregateClass?
  {
    return sweetizer?.getAggregate()
  }

  override fun setAggregate(aggregate: AggregateClass?)
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

  override fun isRefreshingModelAndBinding(): Boolean =
      sweetizer?.isRefreshingModelAndBinding() ?: false

  override fun isFirstLifeCycle(): Boolean =
      sweetizer?.isFirstLifeCycle() ?: false

  override fun isInteracting(): Boolean =
      sweetizer?.isInteracting() ?: false

  override fun isAlive(): Boolean =
      sweetizer?.isAlive() ?: false

  override fun refreshModelAndBind(onOver: Runnable?)
  {
    sweetizer?.refreshModelAndBind(onOver)
  }

  override fun shouldKeepOn(): Boolean =
      sweetizer?.shouldKeepOn() ?: false

  final override fun onRetrieveDisplayObjects()
  {
  }

  fun refreshModelAndBind()
  {
    refreshModelAndBind(null)
  }

  @LayoutRes
  open fun getLayoutId(): Int =
      -1

}