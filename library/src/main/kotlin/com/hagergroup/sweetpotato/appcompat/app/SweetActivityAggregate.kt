package com.hagergroup.sweetpotato.appcompat.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.hagergroup.sweetpotato.app.SweetApplication
import com.hagergroup.sweetpotato.fragment.app.DummySweetFragment
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * The basis class for all Activity Aggregate available in the framework.
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetActivityAggregate(val activity: AppCompatActivity, val activityConfigurable: SweetActivityConfigurable?, val actionBarConfigurable: SweetActionBarConfigurable?)
  : FragmentManager.OnBackStackChangedListener
{

  enum class FragmentTransactionType
  {

    Add, Replace
  }

  init
  {
    activity.supportFragmentManager.addOnBackStackChangedListener(this)
  }

  var openedFragment: SweetFragment<*, *, *>? = null
    private set

  private var lastBackstackFragment: SweetFragment<*, *, *>? = null

  private var lastBackstackCount: Int = 0

  override fun onBackStackChanged()
  {
    val fragmentManager = activity.supportFragmentManager
    val newCount = fragmentManager.backStackEntryCount

    // Fragment just restored from backstack
    if (newCount < lastBackstackCount)
    {
      openedFragment = lastBackstackFragment
    }

    // Save the new backstack count
    lastBackstackCount = newCount

    // Save the new (last) backstack openedFragment
    if (newCount > 1)
    {
      val tag = fragmentManager.getBackStackEntryAt(newCount - 2).name

      if (tag != null)
      {
        lastBackstackFragment = fragmentManager.findFragmentByTag(tag) as SweetFragment<*, *, *>
      }
    }
  }

  /**
   * Replaces the current fragment by the specified fragment one.
   * Reads the activity annotation in order to add it or not to the backstack.
   * The fragment is opened with the extras of the activity as its arguments.
   *
   * @param fragmentClass the fragment to open
   */
  fun replaceFragment(fragmentClass: KClass<out SweetFragment<*, *, *>>)
  {
    addOrReplaceFragment(fragmentClass, activityConfigurable?.fragmentPlaceholderId() ?: -1, activityConfigurable?.addFragmentToBackStack() ?: false, activityConfigurable?.fragmentBackStackName(), null, activity.intent.extras, FragmentTransactionType.Replace)
  }

  /**
   * Replaces the current fragment by the specified fragment one.
   * The fragment is opened with the extras of the activity as its arguments.
   *
   * @param fragmentClass              the fragment to open
   * @param fragmentContainerIdentifer the identifier of the container whose fragment is to be replaced.
   * @param addFragmentToBackStack     indicates wether the fragment should be added to the backstack
   * @param fragmentBackStackName      the name of the fragment into the backstack if it should added
   */
  fun replaceFragment(fragmentClass: KClass<out SweetFragment<*, *, *>>, @IdRes fragmentContainerIdentifer: Int, addFragmentToBackStack: Boolean, fragmentBackStackName: String?)
  {
    addOrReplaceFragment(fragmentClass, fragmentContainerIdentifer, addFragmentToBackStack, fragmentBackStackName, null, activity.intent.extras, FragmentTransactionType.Replace)
  }

  /**
   * Replaces the current fragment by the specified fragment one.
   * Reads the activity annotation in order to add it or not to the backstack.
   *
   * @param fragmentClass the fragment to open
   * @param savedState    the initial saved state of the fragment
   * @param arguments     the arguments of the fragment
   */
  fun replaceFragment(fragmentClass: KClass<out SweetFragment<*, *, *>>, savedState: Fragment.SavedState?, arguments: Bundle?)
  {
    addOrReplaceFragment(fragmentClass, activityConfigurable?.fragmentPlaceholderId() ?: -1, activityConfigurable?.addFragmentToBackStack() ?: false, activityConfigurable?.fragmentBackStackName(), savedState, arguments, FragmentTransactionType.Replace)
  }

  /**
   * Adds or replaces the current fragment by the specified fragment one.
   *
   * @param fragmentClass              the fragment to open
   * @param fragmentContainerIdentifer the identifier of the container whose fragment is to be replaced.
   * @param addFragmentToBackStack     indicates wether the fragment should be added to the backstack
   * @param fragmentBackStackName      the name of the fragment into the backstack if it should added
   * @param savedState                 the initial saved state of the fragment
   * @param arguments                  the arguments of the fragment
   */
  fun addOrReplaceFragment(fragmentClass: KClass<out SweetFragment<*, *, *>>, @IdRes fragmentContainerIdentifer: Int, addFragmentToBackStack: Boolean, fragmentBackStackName: String?, savedState: Fragment.SavedState?, arguments: Bundle?, fragmentTransactionType: FragmentTransactionType)
  {
    try
    {
      openedFragment = fragmentClass.java.newInstance()
      openedFragment?.arguments = arguments

      // We (re)set its initial state if necessary
      if (savedState != null)
      {
        openedFragment?.setInitialSavedState(savedState)
      }

      val fragmentTransaction = activity.supportFragmentManager.beginTransaction()

      if (fragmentTransactionType == FragmentTransactionType.Replace)
      {
        openedFragment?.let {
          fragmentTransaction.replace(fragmentContainerIdentifer, it, if (addFragmentToBackStack == true) fragmentBackStackName else null)
        }
      }
      else
      {
        openedFragment?.let {
          fragmentTransaction.add(fragmentContainerIdentifer, it, if (addFragmentToBackStack == true) fragmentBackStackName else null)
        }
      }

      if (addFragmentToBackStack == true)
      {
        fragmentTransaction.addToBackStack(fragmentBackStackName)
      }

      fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
      fragmentTransaction.commitAllowingStateLoss()
    }
    catch (exception: Exception)
    {
      Timber.e(exception, "Unable to instanciate the openedFragment '${fragmentClass.simpleName}'")
    }
  }

  fun onCreate()
  {
    if (activityConfigurable?.canRotate() == false && SweetApplication.getApplicationConstants<SweetApplication.ApplicationConstants>().canRotate == false)
    {
      // This Activity is not authorized to rotate
      val requestedOrientation = activity.requestedOrientation

      if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
      {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
      }
    }

    openedFragment = null

    activityConfigurable?.fragmentPlaceholderId()?.let {
      openedFragment = activity.supportFragmentManager.findFragmentById(it) as? SweetFragment<*, *, *>
    }

    if (openedFragment == null)
    {
      openParameterFragment()
    }

    actionBarConfigurable?.toolbar()?.let {toolbar ->
        activity.setSupportActionBar(toolbar)
    }

    actionBarConfigurable?.let {
      if (it.actionBarBehavior() == SweetActionBarConfigurable.ActionBarBehavior.Drawer)
      {
        activity.supportActionBar?.apply {
          setDisplayHomeAsUpEnabled(true)
          setDisplayShowHomeEnabled(false)
        }
      }
      else if (it.actionBarBehavior() == SweetActionBarConfigurable.ActionBarBehavior.Up)
      {
        activity.supportActionBar?.apply {
          setDisplayHomeAsUpEnabled(true)
          setDisplayShowHomeEnabled(true)
        }
      }
      else
      {
        activity.supportActionBar?.apply {
          setDisplayHomeAsUpEnabled(false)
          setDisplayShowHomeEnabled(false)
        }
      }
    }
  }

  private fun openParameterFragment()
  {
    activityConfigurable?.let {
      if (it.fragmentClass() != DummySweetFragment::class.java && it.fragmentPlaceholderId() != null)
      {
        replaceFragment(it.fragmentClass())
      }
    }
  }

}
