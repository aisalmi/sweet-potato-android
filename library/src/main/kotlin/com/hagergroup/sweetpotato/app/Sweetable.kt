package com.hagergroup.sweetpotato.app

import com.hagergroup.sweetpotato.lifecycle.SweetLifeCycle

/**
 * All [androidx.appcompat.app.AppCompatActivity] and [androidx.fragment.app.Fragment] entities of the framework must at least implement this composite interface.
 * <p>
 * Any entity implementing this interface is considered as Sweet Potato ready (or Sweet Potato compliant) and benefit from all the framework features.
 * </p>
 * <p>
 * If the implementing entity also implements the [com.hagergroup.sweetpotato.content.SweetBroadcastListener], or the [com.hagergroup.sweetpotato.content.SweetBroadcastListenerProvider] or the
 * [com.hagergroup.sweetpotato.content.SweetBroadcastListenersProvider] interface, the framework will register one or several [android.content.BroadcastReceiver],
 * as explained in the [com.hagergroup.sweetpotato.content.SweetBroadcastListener].
 * </p>
 * <p>
 * When it is required to have an existing [androidx.appcompat.app.AppCompatActivity] or [androidx.fragment.app.Fragment] implement this interface, you may use the
 * [Sweetizer] on that purpose.
 * </p>
 *
 * @param AggregateClass the aggregate class accessible through the [Sweetened.setAggregate] and [Sweetened.getAggregate] methods
 *
 * @see [Sweetizer]
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
interface Sweetable<AggregateClass>
  : Sweetened<AggregateClass>, SweetLifeCycle
