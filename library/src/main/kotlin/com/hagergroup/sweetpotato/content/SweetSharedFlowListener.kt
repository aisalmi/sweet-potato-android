package com.hagergroup.sweetpotato.content

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * This interface is a wrapper around a regular Android native [android.content.BroadcastReceiver], by capturing the
 * [Context.registerReceiver] invocation and implementation is one place. When a [com.hagergroup.sweetpotato.app.Sweetable]
 * entity implements that interface, the framework will register a [android.content.BroadcastReceiver], and
 * unregister automatically during its life cycle.
 * <p>
 * It indicates what kind of broadcast [Intent] are being listened to, and how to handle them, and enables to express both some [Intent]
 * filters, and their consumption at the same place.
 * </p>
 * <p>
 * The framework will request this interface methods during the [androidx.appcompat.app.AppCompatActivity.onCreate] or
 * [androidx.fragment.app.Fragment.onCreate] methods, create a corresponding {[android.content.BroadcastReceiver] and
 * register it}. This created [android.content.BroadcastReceiver] will be unregistered} during the
 * [androidx.appcompat.app.AppCompatActivity.onDestroy] or [androidx.fragment.app.Fragment.onDestroy] methods.
 * </p>
 *
 * @see com.hagergroup.sweetpotato.app.Sweetable.registerSweetSharedFlowListeners
 * @see SweetSharedFlowListenerProvider
 * @see SweetSharedFlowListenersProvider
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
interface SweetSharedFlowListener
{

  /**
   * This method will be invoked by the framework to determine what [IntentFilter] should be associated to the current listener.
   * <p>
   * The returned value of the method will be used to invoke the [Context.registerReceiver] method.
   * </p>
   *
   * @see onCollect
   */
  fun getIntentFilter(): IntentFilter

  /**
   * Is invoked every time an intent that matches is received by the underlying activity.
   *
   * @param intent the broadcast [Intent] which has been received, and which matches the declared [IntentFilter]
   *
   * @see getIntentFilter
   */
  fun onCollect(intent: Intent)

}
