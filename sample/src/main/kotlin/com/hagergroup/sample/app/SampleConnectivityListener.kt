package com.hagergroup.sample.app

import android.content.Context
import com.hagergroup.sweetpotato.app.SweetApplication
import com.hagergroup.sweetpotato.app.SweetConnectivityListener

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
class SampleConnectivityListener(context: Context)
  : SweetConnectivityListener(context)
{

  override fun notifyServices(hasConnectivity: Boolean)
  {
    if (SweetApplication.isOnCreatedDone == false)
    {
      return
    }
  }

}