package com.hagergroup.sweetpotato.appcompat.app

import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar

/**
 * Interface which should be used on a [SweetAppCompatActivity], in order to configure the [Toolbar]/[ActionBar] behavior.
 * <p>
 * When a [SweetAppCompatActivity] uses this interface, the associated [SweetActivityAggregate] will configure the behavior of the [Toolbar]/[ActionBar].
 * </p>
 *
 * @author Ludovic Roland
 * @see SweetActivityAggregate
 * @since 2020.12.18
 */
interface SweetActionBarConfigurable
{

  /**
   * Defines the available [Toolbar]/[ActionBar] "home" button action behaviors handled by the [SweetActivityAggregate].
   */
  enum class ActionBarBehavior
  {

    /**
     * The [Toolbar]/[ActionBar] will display an "up" arrow icon
     */
    Up,

    /**
     * The [Toolbar]/[ActionBar] will display a hamburger icon
     */
    Drawer,

    /**
     * The [Toolbar]/[ActionBar] will display nothing
     */
    None
  }

  /**
   * @return the behavior to be applied to the [Toolbar]/[ActionBar].
   */
  fun actionBarBehavior(): ActionBarBehavior? =
      null

  /**
   * @return the [Toolbar] widget to be used as 'ActionBar'
   */
  fun toolbar(): Toolbar? =
      null

}
