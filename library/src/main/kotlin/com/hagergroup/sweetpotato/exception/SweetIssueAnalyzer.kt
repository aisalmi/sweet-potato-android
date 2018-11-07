package com.hagergroup.sweetpotato.exception

import android.content.Context

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
abstract class SweetIssueAnalyzer(val context: Context)
{

  class DefaultIssueAnalyzer(context: Context)
    : SweetIssueAnalyzer(context)
  {

    override fun handleIssue(throwable: Throwable?): Boolean
    {
      return false
    }

  }

  abstract fun handleIssue(throwable: Throwable?): Boolean

}