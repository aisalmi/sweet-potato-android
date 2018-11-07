package com.hagergroup.sweetpotato.content

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
interface SweetBroadcastListenersProvider
{

  fun getBroadcastListenersCount(): Int

  fun getBroadcastListener(index: Int): SweetBroadcastListener

}