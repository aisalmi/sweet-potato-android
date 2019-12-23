package com.hagergroup.sweetpotato.fragment.app

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.hagergroup.sweetpotato.annotation.SweetFragmentAnnotation
import com.hagergroup.sweetpotato.app.SweetActivityController
import com.hagergroup.sweetpotato.lifecycle.DummySweetViewModel
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * The basis class for all Fragment Aggregate available in the framework.
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
abstract class SweetFragmentAggregate(val fragment: Fragment, private val fragmentAnnotation: Any?)
  : SweetActivityController.Interceptor
{

  interface OnBackPressedListener
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
    fragmentAnnotation?.let {
      activity?.supportActionBar?.let { actionBar ->
        getFragmentTitleIdFromAnnotation()?.let {
          actionBar.setTitle(it)
        }

        getFragmentSubtitleIdFromAnnotation()?.let {
          actionBar.setSubtitle(it)
        }
      }
    }
  }

  @StringRes
  fun getFragmentTitleIdFromAnnotation(): Int?
  {
    return when (fragmentAnnotation)
    {
      is SweetFragmentAnnotation -> fragmentAnnotation.fragmentTitleId
      else                                                                                -> null
    }
  }

  @StringRes
  fun getFragmentSubtitleIdFromAnnotation(): Int?
  {
    return when (fragmentAnnotation)
    {
      is SweetFragmentAnnotation -> fragmentAnnotation.fragmentSubtitleId
      else                                                                                -> null
    }
  }

  @LayoutRes
  fun getFragmentLayoutIdFromAnnotation(): Int?
  {
    return when (fragmentAnnotation)
    {
      is SweetFragmentAnnotation -> fragmentAnnotation.layoutId
      else                                                                                -> null
    }
  }

  fun getFragmentSurviveOnConfigurationChangedFromAnnotation(): Boolean
  {
    return when (fragmentAnnotation)
    {
      is SweetFragmentAnnotation -> fragmentAnnotation.surviveOnConfigurationChanged
      else                                                                                -> false
    }
  }

  fun getViewModelClassFromAnnotation(): Class<out SweetViewModel>
  {
    return when (fragmentAnnotation)
    {
      is SweetFragmentAnnotation -> fragmentAnnotation.viewModelClass.java
      else                                                                                -> DummySweetViewModel::class.java
    }
  }

  fun getPreBindBehaviourFromAnnotation(): Boolean
  {
    return when (fragmentAnnotation)
    {
      is SweetFragmentAnnotation -> fragmentAnnotation.preBind
      else                                                                                -> true
    }
  }

  fun getViewModelContextFromAnnotation(): SweetFragmentAnnotation.ViewModelContext
  {
    return when (fragmentAnnotation)
    {
      is SweetFragmentAnnotation -> fragmentAnnotation.viewModelContext
      else                                                                                -> SweetFragmentAnnotation.ViewModelContext.Fragment
    }
  }

}