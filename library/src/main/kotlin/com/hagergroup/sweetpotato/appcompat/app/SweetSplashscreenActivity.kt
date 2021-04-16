package com.hagergroup.sweetpotato.appcompat.app

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.hagergroup.sweetpotato.app.SweetActivityController
import com.hagergroup.sweetpotato.content.LocalSharedFlowManager
import com.hagergroup.sweetpotato.content.SweetSharedFlowListener
import com.hagergroup.sweetpotato.content.SweetSharedFlowListenerProvider
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import timber.log.Timber
import java.util.*
import kotlin.reflect.KClass

/**
 * A basis activity class which is displayed while the application is loading.
 *
 * @param AggregateClass the aggregate class accessible though the [setAggregate] and [getAggregate] methods
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetSplashscreenActivity<AggregateClass : SweetActivityAggregate, ViewBindingClass : ViewBinding>
  : SweetAppCompatActivity<AggregateClass, ViewBindingClass>(),
    SweetSharedFlowListenerProvider
{

  companion object
  {

    private const val MODEL_LOADED_ACTION = "modelLoadedAction"

    private val initialized = mutableMapOf<String, Date>()

    private var onRetrieveModelCustomStarted = false

    private var onRetrieveModelCustomOver = false

    private var onRetrieveModelCustomOverInvoked = false

    fun isInitialized(activityClass: KClass<out AppCompatActivity>): Date? =
        SweetSplashscreenActivity.initialized[activityClass.java.name]

    fun markAsInitialized(activityClass: KClass<out AppCompatActivity>, isInitialized: Boolean)
    {
      if (isInitialized == false)
      {
        SweetSplashscreenActivity.initialized.remove(activityClass.java.name)
        SweetSplashscreenActivity.onRetrieveModelCustomStarted = false
        SweetSplashscreenActivity.onRetrieveModelCustomOverInvoked = false
      }
      else
      {
        SweetSplashscreenActivity.initialized[activityClass.java.name] = Date()
      }
    }

  }

  private var hasStopped: Boolean = false

  private var onStartRunnable: Runnable? = null

  override fun getSweetSharedFlowListener(): SweetSharedFlowListener
  {
    return object : SweetSharedFlowListener
    {
      override fun getIntentFilter(): IntentFilter
      {
        return IntentFilter(SweetSplashscreenActivity.MODEL_LOADED_ACTION).apply {
          addCategory(packageName)
        }
      }

      override fun onCollect(intent: Intent)
      {
        if (SweetSplashscreenActivity.MODEL_LOADED_ACTION == intent.action)
        {
          markAsInitialized()

          if (isFinishing == false)
          {
            // We do not take into account the event on the activity instance which is over
            if (SweetSplashscreenActivity.onRetrieveModelCustomOverInvoked == false)
            {
              onRetrieveModelCustomOver {
                SweetSplashscreenActivity.onRetrieveModelCustomOverInvoked = true
                finishActivity()
              }
            }
          }
        }
      }
    }
  }

  @Throws(ModelUnavailableException::class)
  protected abstract fun onRetrieveModelCustom()

  override fun onStart()
  {
    super.onStart()

    Timber.d("Marking the splash screen as un-stopped")

    hasStopped = false

    onStartRunnable?.let {
      Timber.d("Starting the delayed activity which follows the splash screen, because the splash screen is restarted")

      it.run()
    }
  }

  @Throws(ModelUnavailableException::class)
  final override suspend fun onRetrieveModel()
  {
    // We check whether another activity instance is already running the business objects retrieval
    if (SweetSplashscreenActivity.onRetrieveModelCustomStarted == false)
    {
      SweetSplashscreenActivity.onRetrieveModelCustomStarted = true
      var onRetrieveModelCustomSuccess = false

      try
      {
        onRetrieveModelCustom()
        onRetrieveModelCustomSuccess = true
      }
      finally
      {
        // If the retrieval of the business objects is a failure, we assume as if it had not been started
        if (onRetrieveModelCustomSuccess == false)
        {
          SweetSplashscreenActivity.onRetrieveModelCustomStarted = false
        }
      }

      SweetSplashscreenActivity.onRetrieveModelCustomOver = true
      LocalSharedFlowManager.emit(lifecycleScope, Intent(SweetSplashscreenActivity.MODEL_LOADED_ACTION).addCategory(packageName))
    }
    else if (SweetSplashscreenActivity.onRetrieveModelCustomOver == true)
    {
      // A previous activity instance has already completed the business objects retrieval, but the current instance was not active at this time
      LocalSharedFlowManager.emit(lifecycleScope, Intent(SweetSplashscreenActivity.MODEL_LOADED_ACTION).addCategory(packageName))
    }
  }

  override fun onBindModel()
  {
  }

  override fun onStop()
  {
    try
    {
      Timber.d("Marking the splash screen as stopped")
      hasStopped = true
    }
    finally
    {
      super.onStop()
    }
  }

  protected open fun onRetrieveModelCustomOver(finishRunnable: Runnable)
  {
    finishRunnable.run()
  }

  protected open fun getNextActivity(): KClass<out AppCompatActivity>? =
      null

  protected open fun startCallingIntent()
  {
    val callingIntent = SweetActivityController.extractCallingIntent(this)

    Timber.d("Redirecting to the initial activity for the component with class '${callingIntent?.component?.className}'")

    // This is essential, in order for the activity to be displayed
    callingIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    try
    {
      startActivity(callingIntent)
    }
    catch (throwable: Throwable)
    {
      Timber.e(throwable, "Cannot start the Activity with Intent '$callingIntent'")
    }

  }

  protected open fun computeNextIntent(): Intent?
  {
    val nextActivity = getNextActivity()

    return if(nextActivity != null)
    {
      Intent(applicationContext, nextActivity.java)
    }
    else
    {
      null
    }
  }

  private fun markAsInitialized()
  {
    SweetSplashscreenActivity.markAsInitialized(this@SweetSplashscreenActivity::class, true)
  }

  protected open fun finishActivity()
  {
    if (isFinishing == false)
    {
      val runnable = Runnable {
        Timber.d("Starting the activity which follows the splash screen")

        if (intent.hasExtra(SweetActivityController.CALLING_INTENT_EXTRA) == true)
        {
          // We only resume the previous activity if the splash screen has not been dismissed
          startCallingIntent()
        }
        else
        {
          // We only resume the previous activity if the splash screen has not been dismissed
          computeNextIntent()?.let {
            startActivity(it)
          }
        }

        Timber.d("Finishing the splash screen")

        finish()
      }
      if (hasStopped == false)
      {
        runnable.run()
      }
      else
      {
        onStartRunnable = runnable

        Timber.d("Delays the starting the activity which follows the splash screen, because the splash screen has been stopped")
      }
    }
    else
    {
      Timber.d("Gives up the starting the activity which follows the splash screen, because the splash screen is finishing or has been stopped")
    }
  }

}
