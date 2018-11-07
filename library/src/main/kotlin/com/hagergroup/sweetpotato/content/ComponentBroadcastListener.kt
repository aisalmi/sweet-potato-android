package com.hagergroup.sweetpotato.content

import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.RestrictTo
import androidx.fragment.app.FragmentActivity

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal abstract class ComponentBroadcastListener
  : SweetBroadcastListener
{

  companion object
  {

    const val VIA_CATEGORIES_EXTRA = "viaCategoriesExtra"

    private const val ACTIVITY_ID_EXTRA = "activityIdExtra"

    private const val COMPONENT_ID_EXTRA = "componentIdExtra"

  }

  var activity: FragmentActivity
    private set

  private val component: Any

  constructor(activity: FragmentActivity) : this(activity, activity)

  constructor(activity: FragmentActivity, component: Any)
  {
    this.activity = activity
    this.component = component
  }

  fun getIntentFilter(viaClass: Boolean): IntentFilter
  {
    val intentFilter = IntentFilter()
    intentFilter.addAction(getAction())

    if (viaClass == true)
    {
      intentFilter.addCategory(activity.javaClass.name)
      intentFilter.addCategory(component.javaClass.name)
    }
    else
    {
      intentFilter.addCategory(Integer.toString(System.identityHashCode(activity)))
      intentFilter.addCategory(Integer.toString(System.identityHashCode(component)))
    }
    return intentFilter
  }

  protected abstract fun getAction(): String

  protected fun matchesIntent(intent: Intent?): Boolean
  {
    if (getAction() == intent?.action)
    {
      if (intent.getBooleanExtra(ComponentBroadcastListener.VIA_CATEGORIES_EXTRA, false) == true || intent.hasExtra(ComponentBroadcastListener.ACTIVITY_ID_EXTRA) == true && System.identityHashCode(activity) == intent.getIntExtra(ComponentBroadcastListener.ACTIVITY_ID_EXTRA, 0) && System.identityHashCode(component) == intent.getIntExtra(ComponentBroadcastListener.COMPONENT_ID_EXTRA, 0) || intent.hasExtra(SweetBroadcastListener.ACTION_ACTIVITY_EXTRA) == true && intent.getStringExtra(SweetBroadcastListener.ACTION_ACTIVITY_EXTRA) == activity.javaClass.name == true && intent.getStringExtra(SweetBroadcastListener.ACTION_COMPONENT_EXTRA) == component.javaClass.name == true)
      {
        // We know that the event deals with the current (activity, component) entities pair
        return true
      }
    }
    return false
  }

}