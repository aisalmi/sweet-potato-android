package com.hagergroup.sweetpotato.content

/**
 * States that the Android [androidx.appcompat.app.AppCompatActivity] or [androidx.fragment.app.Fragment] entity which implements this interface is able to provide a single
 * [SweetSharedFlowListener].
 * <p>
 * As soon as a [com.hagergroup.sweetpotato.app.Sweetable] entity implements this interface, it is able to register a wrapped [android.content.BroadcastReceiver]
 * instance through the concept of [SweetSharedFlowListener]: this is handy, because it enables to integrate an independent reusable
 * [SweetSharedFlowListener] at the same time, and because the framework takes care of unregistering it when the embedding entity is destroyed.
 * </p>
 *
 * @see com.hagergroup.sweetpotato.app.Sweetable.registerSweetSharedFlowListeners
 * @see SweetSharedFlowListener
 * @see SweetSharedFlowListenersProvider
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
fun interface SweetSharedFlowListenerProvider
{

  /**
   * This method will be invoked by the framework for registering a [SweetSharedFlowListener].
   *
   * @return the broadcast listener that this provider exposes
   */
  fun getSweetSharedFlowListener(): SweetSharedFlowListener

}
