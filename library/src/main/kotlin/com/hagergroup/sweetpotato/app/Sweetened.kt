package com.hagergroup.sweetpotato.app

import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hagergroup.sweetpotato.content.SweetBroadcastListener

/**
 * Defines some common methods for all [AppCompatActivity] and [Fragment] entities defined in the framework.
 *
 * @param AggregateClass the aggregate class accessible though the [setAggregate] and [getAggregate] methods
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
interface Sweetened<AggregateClass>
{

  /**
   * Gives access to the entity underlying "aggregate" object.
   * <p>
   * This "aggregate" is especially useful to provide data to the entity, and is typically used by the [SweetActivityController.Interceptor] through
   * the entity life cycle events.
   * </p>
   *
   * @return an object that may be used along the [AppCompatActivity]/[Fragment] entity life
   *
   * @see setAggregate
   */
  fun getAggregate(): AggregateClass?

  /**
   * Enables to set an aggregate hat may be used along the [AppCompatActivity]/[Fragment] entity life.
   *
   * @param aggregate the object to use as an aggregate
   * @see getAggregate
   */
  fun setAggregate(aggregate: AggregateClass?)

  /**
   * Explicitly registers some wrapped broadcast receivers for the [AppCompatActivity]/[Fragment] entity. This method is
   * especially useful to declare and consume at the same place broadcast intents.
   * <p>
   * Those receivers will finally be unregistered by the[AppCompatActivity.onDestroy]/[Fragment.onDestroy] method.
   * </p>
   * <p>
   * When invoking that method, all previously registered listeners via the [com.hagergroup.sweetpotato.content.SweetBroadcastListenerProvider] or
   * [com.hagergroup.sweetpotato.content.SweetBroadcastListenersProvider] are kept, and the new provided ones are added.
   * </p>
   *
   * @param broadcastListeners the wrapped broadcast receivers to registers
   *
   * @see SweetBroadcastListener
   * @see com.hagergroup.sweetpotato.content.SweetBroadcastListenerProvider
   * @see com.hagergroup.sweetpotato.content.SweetBroadcastListenersProvider
   */
  fun registerBroadcastListeners(broadcastListeners: Array<SweetBroadcastListener>)

  /**
   * This is a centralized method which will be invoked by the framework any time an exception is thrown by the entity.
   * <p>
   * It may be also invoked from the implementing entity, when an exception is thrown, so that the [com.hagergroup.sweetpotato.exception.SweetExceptionHandler] handles it.
   * </p>
   *
   * @param fromGuiThread indicates whether the call is done from the GUI thread
   */
  fun onException(throwable: Throwable, fromGuiThread: Boolean)

  /**
   * Gives access to an Android [Handler], which is useful when executing a routine which should be run from the UI thread.
   *
   * @return a valid handler that may be used by the entity for processing GUI-thread operations
   */
  fun getHandler(): Handler

}