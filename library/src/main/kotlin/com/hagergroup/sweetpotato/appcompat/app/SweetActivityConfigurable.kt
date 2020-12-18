package com.hagergroup.sweetpotato.appcompat.app

import androidx.annotation.IdRes
import com.hagergroup.sweetpotato.fragment.app.DummySweetFragment
import com.hagergroup.sweetpotato.fragment.app.SweetFragment
import kotlin.reflect.KClass

/**
 * Interface which should be used on a [SweetAppCompatActivity], in order to configure it.
 *
 * @author Ludovic Roland
 * @since 2020.12.18
 */
interface SweetActivityConfigurable
{

  /**
   * @return the view identifier to be used to place the [SweetFragment] defined by the
   * [com.hagergroup.sweetpotato.appcompat.app.SweetActivityConfigurable.fragmentClass] method.
   */
  @IdRes
  fun fragmentPlaceholderId(): Int? =
      null

  /**
   * @return the [SweetFragment] class to be created and displayed in the [com.hagergroup.sweetpotato.appcompat.app.SweetActivityConfigurable.fragmentPlaceholderId] view holder.
   */
  fun fragmentClass(): KClass<out SweetFragment<*, *, *>> =
      DummySweetFragment::class

  /**
   * @return if the fragment referred into the [com.hagergroup.sweetpotato.appcompat.app.SweetActivityConfigurable.fragmentClass] should be added to the backStack or not.
   */
  fun addFragmentToBackStack(): Boolean =
      false

  /**
   * @return the name of the fragment if it is add to the backStack.
   */
  fun fragmentBackStackName(): String? =
      null

  /**
   * @return true if the activity can rotate.
   */
  fun canRotate(): Boolean =
      false

}
