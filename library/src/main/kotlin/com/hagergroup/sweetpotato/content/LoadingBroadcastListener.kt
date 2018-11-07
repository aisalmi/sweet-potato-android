package com.hagergroup.sweetpotato.content

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.RestrictTo
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal abstract class LoadingBroadcastListener
  : ComponentBroadcastListener
{

  companion object
  {

    fun broadcastLoading(context: Context, targetActivityClass: Class<out FragmentActivity>, targetComponentClass: Class<*>, isLoading: Boolean, addCategory: Boolean)
    {
      val intent = Intent(SweetBroadcastListener.UI_LOAD_ACTION).putExtra(SweetBroadcastListener.UI_LOAD_ACTION_LOADING_EXTRA, isLoading).putExtra(SweetBroadcastListener.ACTION_ACTIVITY_EXTRA, targetActivityClass.name).putExtra(SweetBroadcastListener.ACTION_COMPONENT_EXTRA, targetComponentClass.name)

      if (addCategory == true)
      {
        intent.apply {
          addCategory(targetActivityClass.name)
          addCategory(targetComponentClass.name)
        }
      }

      LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun broadcastLoading(context: Context, targetActivityId: Int, targetComponentId: Int, isLoading: Boolean)
    {
      // The entities hashCode are taken, because this is safe: read the discussion at
      // http://eclipsesource.com/blogs/2012/09/04/the-3-things-you-should-know-about-hashcode/
      val intent = Intent(SweetBroadcastListener.UI_LOAD_ACTION).putExtra(SweetBroadcastListener.UI_LOAD_ACTION_LOADING_EXTRA, isLoading)

      intent.apply {
        putExtra(ComponentBroadcastListener.VIA_CATEGORIES_EXTRA, true)
        addCategory(Integer.toString(targetActivityId))
        addCategory(Integer.toString(targetComponentId))
      }

      LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

  }

  private var counter = 0

  constructor(activity: FragmentActivity) : this(activity, activity)

  constructor(activity: FragmentActivity, component: Any) : super(activity, component)

  override fun getIntentFilter(): IntentFilter =
      getIntentFilter(false)

  @Synchronized
  override fun onReceive(context: Context?, intent: Intent?)
  {
    if (matchesIntent(intent) == true)
    {
      // We know that the event deals with the current (activity, component) pair
      val wasLoading = counter >= 1

      // We only take into account the loading event coming from the activity itself
      val isLoading = intent?.getBooleanExtra(SweetBroadcastListener.UI_LOAD_ACTION_LOADING_EXTRA, true)
      counter += if (isLoading == true) 1 else -1
      val isNowLoading = counter >= 1

      // We only trigger an event provided the cumulative loading status has changed
      if (wasLoading != isNowLoading)
      {
        onLoading(isNowLoading)
      }
    }
  }

  override fun getAction(): String =
      SweetBroadcastListener.UI_LOAD_ACTION

  protected abstract fun onLoading(isLoading: Boolean)

}