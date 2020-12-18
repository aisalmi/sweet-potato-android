package com.hagergroup.sweetpotato.fragment.app

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * The basis class for all Fragment Aggregate available in the framework.
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetFragmentAggregate(val fragment: Fragment, private val fragmentConfigurable: SweetFragmentConfigurable?)
{

  fun interface OnBackPressedListener
  {

    fun onBackPressed(): Boolean

  }

  /**
   * Open the specified fragment, the previous fragment is add to the back stack.
   */
  fun openChildFragment(parentFragment: SweetFragment<*, *, *>, @IdRes fragmentPlaceholderIdentifier: Int, fragmentClass: KClass<SweetFragment<*, *, *>>, savedState: Fragment.SavedState?)
  {
    try
    {
      val fragmentTransaction = parentFragment.childFragmentManager.beginTransaction()
      val childFragment = fragmentClass.java.newInstance()
      childFragment.arguments = parentFragment.arguments

      // We (re)set its initial state if necessary
      if (savedState != null)
      {
        childFragment.setInitialSavedState(savedState)
      }

      fragmentTransaction.replace(fragmentPlaceholderIdentifier, childFragment)
      fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
      fragmentTransaction.commit()
    }
    catch (exception: Exception)
    {
      Timber.e(exception, "Unable to open the fragment '${fragmentClass.simpleName}'")
    }
  }

  fun onCreate(activity: AppCompatActivity?)
  {
    fragmentConfigurable?.let {
      activity?.supportActionBar?.let { actionBar ->
        it.fragmentTitleId()?.let { toolbarTitleId ->
          actionBar.setTitle(toolbarTitleId)
        }

        it.fragmentSubtitleId()?.let { toolbarSubtitleId ->
          actionBar.setSubtitle(toolbarSubtitleId)
        }
      }
    }
  }

}