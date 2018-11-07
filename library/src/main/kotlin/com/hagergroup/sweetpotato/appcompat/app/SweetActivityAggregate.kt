package com.hagergroup.sweetpotato.appcompat.app

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.hagergroup.sweetpotato.annotation.SweetActivityAnnotation
import com.hagergroup.sweetpotato.app.SweetApplication
import com.hagergroup.sweetpotato.fragment.app.DummySweetFragment
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import timber.log.Timber
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetActivityAggregate<ApplicationClass : SweetApplication>(val activity: FragmentActivity, val activityAnnotation: SweetActivityAnnotation)
  : FragmentManager.OnBackStackChangedListener
{

  enum class FragmentTransactionType
  {

    Add, Replace
  }

  protected var fragment: SweetFragment<*>? = null

  private var lastBackstackFragment: SweetFragment<*>? = null

  private var lastBackstackCount: Int = 0

  init
  {
    activity.supportFragmentManager.addOnBackStackChangedListener(this)
  }

  override fun onBackStackChanged()
  {
    val fragmentManager = activity.supportFragmentManager
    val newCount = fragmentManager.backStackEntryCount

    // Fragment just restored from backstack
    if (newCount < lastBackstackCount)
    {
      fragment = lastBackstackFragment
    }

    // Save the new backstack count
    lastBackstackCount = newCount

    // Save the new (last) backstack fragment
    if (newCount > 1)
    {
      val tag = fragmentManager.getBackStackEntryAt(newCount - 2).name

      if (tag != null)
      {
        lastBackstackFragment = fragmentManager.findFragmentByTag(tag) as SweetFragment<*>
      }
    }
  }

  fun getApplication(): ApplicationClass? =
      activity.application as? ApplicationClass

  fun replaceFragment(fragmentClass: KClass<out SweetFragment<*>>)
  {
    addOrReplaceFragment(fragmentClass, activityAnnotation.fragmentPlaceholderId, activityAnnotation.addFragmentToBackStack, activityAnnotation.fragmentBackStackName, null, activity.intent.extras, FragmentTransactionType.Replace)
  }

  fun replaceFragment(fragmentClass: KClass<out SweetFragment<*>>, @IdRes fragmentContainerIdentifer: Int, addFragmentToBackStack: Boolean, fragmentBackStackName: String?)
  {
    addOrReplaceFragment(fragmentClass, fragmentContainerIdentifer, addFragmentToBackStack, fragmentBackStackName, null, activity.intent.extras, FragmentTransactionType.Replace)
  }

  fun replaceFragment(fragmentClass: KClass<out SweetFragment<*>>, savedState: Fragment.SavedState, arguments: Bundle?)
  {
    addOrReplaceFragment(fragmentClass, activityAnnotation.fragmentPlaceholderId, activityAnnotation.addFragmentToBackStack, activityAnnotation.fragmentBackStackName, savedState, arguments, FragmentTransactionType.Replace)
  }

  fun addOrReplaceFragment(fragmentClass: KClass<out SweetFragment<*>>, @IdRes fragmentContainerIdentifer: Int, addFragmentToBackStack: Boolean, fragmentBackStackName: String?, savedState: Fragment.SavedState?, arguments: Bundle?, fragmentTransactionType: FragmentTransactionType)
  {
    try
    {
      fragment = fragmentClass.createInstance()
      fragment?.arguments = arguments

      // We (re)set its initial state if necessary
      if (savedState != null)
      {
        fragment?.setInitialSavedState(savedState)
      }

      val fragmentTransaction = activity.supportFragmentManager.beginTransaction()

      if (fragmentTransactionType == FragmentTransactionType.Replace)
      {
        fragment?.let {
          fragmentTransaction.replace(fragmentContainerIdentifer, it, if (addFragmentToBackStack == true) fragmentBackStackName else null)
        }
      }
      else
      {
        fragment?.let {
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
      Timber.e(exception, "Unable to instanciate the fragment '${fragmentClass.simpleName}'")
    }

  }

  fun getOpenedFragment(): SweetFragment<*>? =
      fragment

  fun onCreate()
  {
    activity.setContentView(activityAnnotation.contentViewId)

    fragment = activity.supportFragmentManager.findFragmentById(activityAnnotation.fragmentPlaceholderId) as? SweetFragment<*>

    fragment?.let {
      openParameterFragment()
    }
  }

  private fun openParameterFragment()
  {
    if (activityAnnotation.fragmentClass != DummySweetFragment::class.java && activityAnnotation.fragmentPlaceholderId != -1)
    {
      replaceFragment(activityAnnotation.fragmentClass)
    }
  }

}