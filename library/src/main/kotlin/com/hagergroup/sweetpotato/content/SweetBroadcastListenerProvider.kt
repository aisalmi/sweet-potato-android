package com.hagergroup.sweetpotato.content

/**
 * States that the Android [androidx.appcompat.app.AppCompatActivity] or [androidx.fragment.app.Fragment] entity which implements this interface is able to provide a single
 * [SweetBroadcastListener].
 * <p>
 * As soon as a [com.hagergroup.sweetpotato.app.Sweetable] entity implements this interface, it is able to register a wrapped [android.content.BroadcastReceiver]
 * instance through the concept of [SweetBroadcastListener]: this is handy, because it enables to integrate an independent reusable
 * [SweetBroadcastListener] at the same time, and because the framework takes care of unregistering it when the embedding entity is destroyed.
 * </p>
 *
 * @see com.hagergroup.sweetpotato.app.Sweetable.registerBroadcastListeners
 * @see SweetBroadcastListener
 * @see SweetBroadcastListenersProvider
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
fun interface SweetBroadcastListenerProvider
{

  /**
   * This method will be invoked by the framework for registering a [SweetBroadcastListener].
   *
   * @return the broadcast listener that this provider exposes
   */
  fun getBroadcastListener(): SweetBroadcastListener

}