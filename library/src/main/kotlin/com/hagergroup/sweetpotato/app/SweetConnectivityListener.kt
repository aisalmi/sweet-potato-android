package com.hagergroup.sweetpotato.app

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hagergroup.sweetpotato.content.SweetBroadcastListener
import timber.log.Timber
import java.util.*

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
abstract class SweetConnectivityListener(val context: Context)
  : SweetActivityController.SweetInterceptor
{

  companion object
  {

    const val CONNECTIVITY_CHANGED_ACTION = "connectivityChangedAction"

    const val EXTRA_HAS_CONNECTIVITY = "hasConnectivity"

  }

  private var hasConnectivity = true

  private var networkCallback: ConnectivityManager.NetworkCallback? = null

  private var activitiesCount: Int = 0

  private val networkStatus = HashMap<String, Boolean>()

  init
  {
    activitiesCount = 0

    // We immediately extract the connectivity status
    val activeNetworkInfo = getActiveNetworkInfo()

    if (activeNetworkInfo?.isConnected == false)
    {
      Timber.i("The Internet connection is off")

      hasConnectivity = false
      notifyServices(hasConnectivity)
    }
  }

  override fun onLifeCycleEvent(activity: FragmentActivity?, component: Any?, event: Lifecycle.Event)
  {
    if (event == Lifecycle.Event.ON_CREATE)
    {
      // We listen to the network connection potential issues: we do not want child activities to also register for the connectivity change events
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
      {
        registerBroadcastListenerLegacy(activity, component)
      }
      else
      {
        registerBroadcastListener(activity, component)
      }
    }
    else if (event == Lifecycle.Event.ON_DESTROY)
    {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
      {
        unregisterBroadcastListener(activity, component)
      }
    }
  }

  fun hasConnectivity(): Boolean =
      hasConnectivity

  fun getActiveNetworkInfo(): NetworkInfo? =
      getConnectivityManager().activeNetworkInfo

  protected abstract fun notifyServices(hasConnectivity: Boolean)


  protected fun getConnectivityManager(): ConnectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  private fun registerBroadcastListenerLegacy(activity: FragmentActivity?, component: Any?)
  {
    // We listen to the network connection potential issues: we do not want child activities to also register for the connectivity change events
    if (component == null && activity?.parent == null)
    {
      val broadcastListener = object : SweetBroadcastListener
      {

        override fun getIntentFilter(): IntentFilter =
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

        override fun onReceive(context: Context?, intent: Intent?)
        {
          val previousConnectivity = hasConnectivity
          hasConnectivity = intent?.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false) == false
          handleConnectivityChange(previousConnectivity)
        }

      }

      (activity as? Sweetened<*>)?.registerBroadcastListeners(arrayOf(broadcastListener))
    }
  }


  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private fun registerBroadcastListener(activity: FragmentActivity?, component: Any?)
  {
    // We listen to the network connection potential issues: we do not want child Activities to also register for the connectivity change events
    if (component == null && activity?.parent == null)
    {
      activitiesCount++

      // No need to synchronize this scope, because the method is invoked from the UI thread
      if (networkCallback == null)
      {
        val builder = NetworkRequest.Builder()

        networkCallback = object : ConnectivityManager.NetworkCallback()
        {

          override fun onAvailable(network: Network)
          {
            networkStatus[network.toString()] = true
            onNetworkChangedLollipopAndAbove(networkStatus.containsValue(true))
          }

          override fun onLost(network: Network)
          {
            networkStatus.remove(network.toString())
            onNetworkChangedLollipopAndAbove(networkStatus.containsValue(true))
          }

        }

        getConnectivityManager().registerNetworkCallback(builder.build(), networkCallback)

        Timber.d("Registered the Lollipop network callback")
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private fun unregisterBroadcastListener(activity: FragmentActivity?, component: Any?)
  {
    // We listen to the network connection potential issues: we do not want child activities to also register for the connectivity change events
    if (component == null && activity?.parent == null)
    {
      activitiesCount--

      if (activitiesCount <= 0)
      {
        if (networkCallback != null)
        {
          getConnectivityManager().unregisterNetworkCallback(networkCallback)

          Timber.d("Unregisters the Lollipop network callback")

          networkCallback = null
        }
      }
    }
  }


  private fun onNetworkChangedLollipopAndAbove(hasConnectivity: Boolean)
  {
    val previousConnectivity = this.hasConnectivity
    this.hasConnectivity = hasConnectivity

    handleConnectivityChange(previousConnectivity)
  }

  private fun handleConnectivityChange(previousConnectivity: Boolean)
  {
    if (previousConnectivity != hasConnectivity)
    {
      // With this filter, only one broadcast listener will handle the event
      Timber.i("Received an Internet connectivity change event: the connection is now '$hasConnectivity'")

      // We notify the application regarding this connectivity change event
      LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(SweetConnectivityListener.CONNECTIVITY_CHANGED_ACTION).putExtra(SweetConnectivityListener.EXTRA_HAS_CONNECTIVITY, hasConnectivity))

      notifyServices(hasConnectivity)
    }
  }

}