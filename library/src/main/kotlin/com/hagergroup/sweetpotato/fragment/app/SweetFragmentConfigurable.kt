package com.hagergroup.sweetpotato.fragment.app

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel

/**
 * Interface which should be used on a [SweetFragment], in order to configure it.
 *
 * @author Ludovic Roland
 * @since 2020.12.18
 */
interface SweetFragmentConfigurable
{

  /**
   * Defines the context which should be attached to the [SweetViewModel] holds by the [SweetFragment].
   */
  enum class ViewModelContext
  {

    /**
     * The [SweetViewModel] should be attached to the fragment
     */
    Fragment,

    /**
     * The [SweetViewModel] should be attached to the activity
     */
    Activity
  }

  /**
   * @return the string identifier to be used on [ActionBar.setTitle].
   */
  @StringRes
  fun fragmentTitleId(): Int? =
      null

  /**
   * @return the string identifier to be setted on [ActionBar.setSubtitle].
   */
  @StringRes
  fun fragmentSubtitleId(): Int? =
      null

  /**
   * @return the layout identifier to be used in the
   * [SweetFragment.onCreateView] method.
   */
  @LayoutRes
  fun layoutId(): Int? =
      null

  /**
   * @return true if databinding should be done a first time before setting the model
   */
  fun preBind(): Boolean =
      false

  /**
   * @return the context to be attached to the [SweetViewModel].
   */
  fun viewModelContext(): ViewModelContext =
      ViewModelContext.Fragment

}
