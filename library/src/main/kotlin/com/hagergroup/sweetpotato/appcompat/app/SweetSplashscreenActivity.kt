package com.hagergroup.sweetpotato.appcompat.app

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hagergroup.sweetpotato.app.SweetActivityController
import com.hagergroup.sweetpotato.content.SweetBroadcastListener
import com.hagergroup.sweetpotato.lifecycle.ViewModelUnavailableException
import timber.log.Timber
import java.util.*
import kotlin.reflect.KClass

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetSplashscreenActivity<AggregateClass : SweetActivityAggregate>
  : SweetAppCompatActivity<AggregateClass>(),
    SweetBroadcastListener
{

  companion object
  {

    private const val VIEW_MODEL_LOADED_ACTION = "ViewModelLoadedAction"

    private val initialized = HashMap<String, Date>()

    private var onRetrieveViewModelCustomStarted = false

    private var onRetrieveViewModelCustomOver = false

    private var onRetrieveViewModelCustomOverInvoked = false

    fun isInitialized(activityClass: Class<out FragmentActivity>): Date? =
        SweetSplashscreenActivity.initialized[activityClass.name]

    fun markAsInitialized(activityClass: Class<out FragmentActivity>, isInitialized: Boolean)
    {
      if (isInitialized == false)
      {
        SweetSplashscreenActivity.initialized.remove(activityClass.name)
        SweetSplashscreenActivity.onRetrieveViewModelCustomStarted = false
        SweetSplashscreenActivity.onRetrieveViewModelCustomOverInvoked = false
      }
      else
      {
        SweetSplashscreenActivity.initialized[activityClass.name] = Date()
      }
    }

  }

  private var hasStopped: Boolean = false

  private var onStartRunnable: Runnable? = null

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

  @Throws(ViewModelUnavailableException::class)
  final override suspend fun onRetrieveViewModel()
  {
    // We check whether another activity instance is already running the business objects retrieval
    if (SweetSplashscreenActivity.onRetrieveViewModelCustomStarted == false)
    {
      SweetSplashscreenActivity.onRetrieveViewModelCustomStarted = true
      var onRetrieveBusinessObjectsCustomSuccess = false

      try
      {
        onRetrieveViewModelCustom()
        onRetrieveBusinessObjectsCustomSuccess = true
      }
      finally
      {
        // If the retrieval of the business objects is a failure, we assume as if it had not been started
        if (onRetrieveBusinessObjectsCustomSuccess == false)
        {
          SweetSplashscreenActivity.onRetrieveViewModelCustomStarted = false
        }
      }

      SweetSplashscreenActivity.onRetrieveViewModelCustomOver = true
      LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(Intent(SweetSplashscreenActivity.VIEW_MODEL_LOADED_ACTION).addCategory(packageName))
    }
    else if (SweetSplashscreenActivity.onRetrieveViewModelCustomOver == true)
    {
      // A previous activity instance has already completed the business objects retrieval, but the current instance was not active at this time
      LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(Intent(SweetSplashscreenActivity.VIEW_MODEL_LOADED_ACTION).addCategory(packageName))
    }
  }

  override fun onBindViewModel()
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

  override fun getIntentFilter(): IntentFilter
  {
    return IntentFilter(SweetSplashscreenActivity.VIEW_MODEL_LOADED_ACTION).apply {
      addCategory(packageName)
    }
  }

  override fun onReceive(context: Context?, intent: Intent?)
  {
    if (SweetSplashscreenActivity.VIEW_MODEL_LOADED_ACTION == intent?.action)
    {
      markAsInitialized()

      if (isFinishing == false)
      {
        // We do not take into account the event on the activity instance which is over
        if (SweetSplashscreenActivity.onRetrieveViewModelCustomOverInvoked == false)
        {
          onRetrieveViewModelCustomOver(Runnable {
            SweetSplashscreenActivity.onRetrieveViewModelCustomOverInvoked = true
            finishActivity()
          })
        }
      }
    }
  }

  protected abstract fun getNextActivity(): KClass<out FragmentActivity>

  @Throws(ViewModelUnavailableException::class)
  protected abstract fun onRetrieveViewModelCustom()

  protected open fun onRetrieveViewModelCustomOver(finishRunnable: Runnable)
  {
    finishRunnable.run()
  }

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

  protected open fun computeNextIntent(): Intent =
      Intent(applicationContext, getNextActivity().java)

  private fun markAsInitialized()
  {
    SweetSplashscreenActivity.markAsInitialized(this@SweetSplashscreenActivity::class.java, true)
  }

  private fun finishActivity()
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
          startActivity(computeNextIntent())
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