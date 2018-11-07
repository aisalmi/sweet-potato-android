package com.hagergroup.sweetpotato.app

import android.os.Handler
import com.hagergroup.sweetpotato.content.SweetBroadcastListener

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
interface Sweetened<AggregateClass>
{

  fun getAggregate(): AggregateClass?

  fun setAggregate(aggregate: AggregateClass)

  fun registerBroadcastListeners(broadcastListeners: Array<SweetBroadcastListener>)

  fun onException(throwable: Throwable, fromGuiThread: Boolean)

  fun getHandler(): Handler

}