package com.hagergroup.sweetpotato.exception

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException

/**
 * @author Ludovic Roland
 * @since 2018.11.05
 */
interface SweetExceptionHandler
{

  fun onModelUnavailableException(activity: FragmentActivity?, fragment: Fragment?, exception: ModelUnavailableException): Boolean

  fun onActivityException(activity: FragmentActivity?, fragment: Fragment?, throwable: Throwable): Boolean

  fun onContextException(isRecoverable: Boolean, context: Context?, throwable: Throwable): Boolean

  fun onException(isRecoverable: Boolean, throwable: Throwable): Boolean

  fun reportIssueIfNecessary(isRecoverable: Boolean, throwable: Throwable)

}