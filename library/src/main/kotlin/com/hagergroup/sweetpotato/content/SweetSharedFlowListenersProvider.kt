package com.hagergroup.sweetpotato.content

/**
 * States that the Android [androidx.appcompat.app.AppCompatActivity] or [androidx.fragment.app.Fragment] entity which implements this interface is able to provide several
 * [SweetSharedFlowListener].
 * <p>
 * As soon as a [com.hagergroup.sweetpotato.app.Sweetable] entity implements this interface, it is able to register several wrapped  [android.content.BroadcastReceiver]
 * instances through the concept of [SweetSharedFlowListener]: this is handy, because it enables to aggregate several independent reusable
 * [SweetSharedFlowListener] at the same time, and because the framework takes care of unregistering them when the embedding entity is destroyed.
 * </p>
 * <p>
 * This interface has been split into two distinct methods, one for determining how many broadcast listeners the entity exposes,
 * one for getting each individual [SweetSharedFlowListener]. This split is mostly due to performance issues.
 * </p>
 *
 * @see com.hagergroup.sweetpotato.app.Sweetable.registerBroadcastListeners
 * @see SweetSharedFlowListener
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
interface SweetSharedFlowListenersProvider
{

  /**
   * This method will be invoked by the framework, so that it knows how many [SweetSharedFlowListener] it exposes.
   *
   * @return the number of [SweetSharedFlowListener] which are supported
   *
   * @see getSharedFlowListener
   */
  fun getSharedFlowListenersCount(): Int

  /**
   * This method is bound to be invoked successfully by the framework with a `index` argument ranging from `0` to
   * `getBroadcastListenersCount() - 1`. The method implementation is responsible for returning all the [SweetSharedFlowListener]
   * that this entity is supposed to expose.
   *
   * @param index of the [SweetSharedFlowListener] to return
   *
   * @return the [SweetSharedFlowListener] for the provided `index` parameter
   *
   * @see getSharedFlowListenersCount
   */
  fun getSharedFlowListener(index: Int): SweetSharedFlowListener

}
