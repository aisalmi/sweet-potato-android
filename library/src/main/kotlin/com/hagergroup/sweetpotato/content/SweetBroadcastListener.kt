package com.hagergroup.sweetpotato.content

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
interface SweetBroadcastListener
{

  companion object
  {

    const val ACTION_ACTIVITY_EXTRA = "actionActivityExtra"

    const val ACTION_COMPONENT_EXTRA = "actionComponentExtra"

    const val UI_LOAD_ACTION_LOADING_EXTRA = "uiLoadActionLoadingExtra"

    const val UI_LOAD_ACTION = "uiLoadAction"

    const val UPDATE_ACTION = "updateAction"

    const val RELOAD_ACTION = "reloadAction"

  }

  fun getIntentFilter(): IntentFilter

  fun onReceive(context: Context?, intent: Intent?)

}