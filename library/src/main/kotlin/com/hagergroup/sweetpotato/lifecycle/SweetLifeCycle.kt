package com.hagergroup.sweetpotato.lifecycle

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
interface SweetLifeCycle
{

  fun onRetrieveDisplayObjects()

  @Throws(ModelUnavailableException::class)
  fun onRetrieveModel()

  fun onBindModel()

  fun refreshModelAndBind(onOver: Runnable?)

  fun shouldKeepOn(): Boolean

  fun isFirstLifeCycle(): Boolean

  fun isInteracting(): Boolean

  fun isAlive(): Boolean

  fun isRefreshingModelAndBinding(): Boolean

}