package com.hagergroup.sweetpotato.util

import android.util.Log
import timber.log.Timber

/**
 * @author Ludovic Roland
 * @since 2018.11.05
 */
class SweetLogTree
  : Timber.Tree()
{

  companion object
  {

    private const val MAX_LOG_LENGTH = 4000

  }

  override fun isLoggable(tag: String?, priority: Int): Boolean
  {
    if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO)
    {
      return false
    }

    return true
  }

  override fun log(priority: Int, tag: String?, message: String, t: Throwable?)
  {
    if (isLoggable(tag, priority) == true)
    {
      // Message is short enough, doesn't need to be broken into chunks
      if (message.length < MAX_LOG_LENGTH)
      {
        if (priority == Log.ASSERT)
        {
          Log.wtf(tag, message)
        }
        else
        {
          Log.println(priority, tag, message)
        }

        return
      }

      var i = 0
      val length = message.length

      while (i < length)
      {
        var newline = message.indexOf('\n', i)
        newline = if (newline != -1) newline else length

        do
        {
          val end = Math.min(newline, i + MAX_LOG_LENGTH)
          val part = message.substring(i, end)

          if (priority == Log.ASSERT)
          {
            Log.wtf(tag, part)
          }
          else
          {
            Log.println(priority, tag, part)
          }

          i = end
        }
        while (i < newline)

        i++
      }
    }
  }

}