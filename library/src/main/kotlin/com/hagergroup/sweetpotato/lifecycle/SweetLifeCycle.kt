package com.hagergroup.sweetpotato.lifecycle

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
interface SweetLifeCycle
{

  @Throws(ViewModelUnavailableException::class)
  suspend fun onRetrieveViewModel()

  fun onBindViewModel()

  fun refreshViewModelAndBind(onOver: Runnable?)

  fun shouldKeepOn(): Boolean

  fun isFirstLifeCycle(): Boolean

  fun isInteracting(): Boolean

  fun isAlive(): Boolean

  fun isRefreshingViewModelAndBinding(): Boolean

}