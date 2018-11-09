package com.hagergroup.sample

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import com.hagergroup.sample.app.SampleActivityAggregate
import com.hagergroup.sample.app.SampleFragmentAggregate
import com.hagergroup.sample.fragment.SampleFragment
import com.hagergroup.sweetpotato.annotation.SweetLoadingAndErrorAnnotation
import com.hagergroup.sweetpotato.app.SweetApplication
import com.hagergroup.sweetpotato.app.SweetLoadingAndErrorInterceptor
import com.hagergroup.sweetpotato.app.Sweetable
import com.hagergroup.sweetpotato.appcompat.app.SweetAppCompatActivity
import com.hagergroup.sweetpotato.fragment.app.SweetFragmentAggregate
import com.hagergroup.sweetpotato.lifecycle.ModelUnavailableException
import timber.log.Timber
import java.util.*

/**
 * @author Ludovic Roland
 * @since 2018.11.08
 */
@SweetLoadingAndErrorAnnotation(enabled = false, loadingEnabled = false)
abstract class SampleActivity
  : SweetAppCompatActivity<SampleActivityAggregate>(),
    SweetLoadingAndErrorInterceptor.ModelUnavailableReporter<SampleFragmentAggregate>
{

  private val modelUnavailableFragments = Collections.synchronizedSet(HashSet<Sweetable<SampleFragmentAggregate>>())

  private var hasBeenPaused = false

  private var setBackMainFragmentRequired = false

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    if (getAggregate()?.activityAnnotation?.canRotate == false && SweetApplication.applicationConstants.canRotate == false)
    {
      // This Activity is not authorized to rotate
      val requestedOrientation = requestedOrientation

      if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
      {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean
  {
    return when (item.itemId)
    {
      android.R.id.home ->
      {
        //In order to respect the Android navigation guidelines, we should use the NavUtils class but...
        // NavUtils.navigateUpFromSameTask(this);
        finish()
        super.onOptionsItemSelected(item)
      }
      else              ->
      {
        super.onOptionsItemSelected(item)
      }
    }
  }

  @Throws(ModelUnavailableException::class)
  override fun onRetrieveModel()
  {
  }

  override fun onBindModel()
  {
  }

  override fun onConfigurationChanged(newConfig: Configuration)
  {
    super.onConfigurationChanged(newConfig)

    // This work-around is there to fix the issue when the device orientation has been changed on a stacked Activity, and that the hereby Activity
    // resumes with the new orientation
    if (hasBeenPaused == true)
    {
      setBackMainFragmentRequired = true
    }
    else
    {
      setBackMainFragmentFollowingOnConfigurationChanged()
    }
  }

  override fun onBackPressed()
  {
    val currentFragment = getAggregate()?.openedFragment

    if (currentFragment is SweetFragmentAggregate.OnBackPressedListener)
    {
      if (currentFragment.onBackPressed() == true)
      {
        return
      }
    }

    if (isFinishing == false)
    {
      super.onBackPressed()
    }
  }

  override fun reportModelUnavailableException(fragment: Sweetable<SampleFragmentAggregate>, modelUnavailableException: ModelUnavailableException)
  {
    fragment.getAggregate()?.rememberModelUnavailableException(modelUnavailableException)
    modelUnavailableFragments.add(fragment)
    fragment.getAggregate()?.getLoadingErrorAndRetryAggregate()?.showException(this, modelUnavailableException, Runnable {
      val copiedFragments = HashSet(modelUnavailableFragments)
      modelUnavailableFragments.clear()

      copiedFragments.forEach {
        it.getAggregate()?.forgetException()
        it.refreshModelAndBind(null)
      }
    })
  }

  override fun onResumeFragments()
  {
    super.onResumeFragments()

    hasBeenPaused = false

    if (setBackMainFragmentRequired)
    {
      setBackMainFragmentRequired = false
      setBackMainFragmentFollowingOnConfigurationChanged()
    }
  }

  private fun setBackMainFragmentFollowingOnConfigurationChanged()
  {
    val openedFragment = getAggregate()?.openedFragment

    if (openedFragment is SampleFragment)
    {
      try
      {
        if (openedFragment.getAggregate()?.fragmentAnnotation?.surviveOnConfigurationChanged == true)
        {
          Timber.d("The Fragment from class '${openedFragment.javaClass.name}' will not be recreated, because it is declared as able to survive a configuration change")
          return
        }
      }
      catch (exception: NullPointerException)
      {
        // Catch the NullPointerException if the openedFragment does not contain a sweetizer object !
        Timber.w(exception, "Unable to get the aggregate attached to the Fragment!")
      }
    }

    if (openedFragment != null)
    {
      try
      {
        val savedState = supportFragmentManager.saveFragmentInstanceState(openedFragment)
        val arguments = openedFragment.arguments
        getAggregate()?.replaceFragment(openedFragment::class, savedState, arguments)
      }
      catch (exception: IllegalStateException)
      {
        Timber.w(exception, "Unable to retrieve the FragmentManager from main Fragment!")
      }
    }
  }

}
