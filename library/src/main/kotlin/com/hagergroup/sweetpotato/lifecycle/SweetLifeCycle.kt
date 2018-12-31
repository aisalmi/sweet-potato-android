package com.hagergroup.sweetpotato.lifecycle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * Identifies a typical life cycle work-flow for an [AppCompatActivity] or a [Fragment] of the framework. When referring to the
 * [AppCompatActivity] "life cycle", we do not consider the entity instance re-creation due to <a href="http://developer.android.com/reference/android/app/Activity.html#ConfigurationChanges">configuration changes</a>,
 * for instance. The framework capture those entities work-flow and divides it into typical actions:
 * <p>
 * <ol>
 * <li>set the layout and extract the key widgets,</li>
 * <li>retrieve the business objects are represented on the entity,</li>
 * <li>bind the entity widgets to the previously extracted business objects,</li>
 * <li>when an [AppCompatActivity] has been stacked over the current entity task, and the navigation comes back to the entity, refresh the widgets with
 * the potential business objects new values.</li>
 * </ol>
 * </p>
 * <p>
 * When deriving from this interface, just implement this interface method. You do not need to override the traditional [AppCompatActivity]
 * {[AppCompatActivity.onCreate]/[AppCompatActivity.onStart]/[AppCompatActivity.onResume] method nor the [Fragment]
 * [Fragment.onCreate]/[Fragment.onStart]/[Fragment.onResume] methods, even if you still are allowed to.
 * </p>
 * <p>
 * The `onXXX` methods should never be invoked because they are callbacks, and that only the framework should invoke them during the entity life
 * cycle!
 * </p>
 * <p>
 * In the code, the interface methods are sorted in chronological order of invocation by the framework.
 * </p>
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
interface SweetLifeCycle
{

  /**
   * This is the place where the derived class should [AppCompatActivity.setContentView] set its layout}, extract all [android.view.View]
   * which require a further customization and store them as instance attributes. This method is invoked only once during the entity life cycle.
   * <p>
   * The method is invoked:
   * </p>
   * <ul>
   * <li>for an [AppCompatActivity], during the [AppCompatActivity.onCreate] execution, after the parent [AppCompatActivity.onCreate] method has been
   * invoked ;</li>
   * <li>for an [Fragment], during the [Fragment.onCreate] execution, after the parent [Fragment.onCreate] method has been
   * invoked.</li>
   * </ul>
   * <p>
   * It is ensured that this method will be invoked from the UI thread!
   * </p>
   * <p>
   * Never invoke this method, only the framework should, because this is a callback!
   * </p>
   */
  fun onRetrieveDisplayObjects()

  /**
   * This is the place where to load the business objects, from memory, local persistence, via web services, necessary for the entity processing.
   * <p>
   * This callback will be invoked from a background thread, and not the UI thread. This method will be invoked a first time once the entity has successfully
   * retrieved its display objects, and every time the [refreshModelAndBind] method is invoked.
   * </p>
   * <p>
   * When the method is invoked the first time, it is ensured that this method will be invoked at least after the entity `onResume()` execution has started.
   * </p>
   * <p>
   * It is ensured to be invoked from a background thread.
   * </p>
   * <p>
   * Never invoke this method, only the framework should, because this is a callback!
   * </p>
   *
   * @throws ModelUnavailableException if the extraction of the business objects was a failure and that this issue cannot be recovered, this enables to notify the framework
   * that the current entity cannot continue its execution
   */
  @Throws(ModelUnavailableException::class)
  fun onRetrieveModel()

  fun onBindModel()

  fun refreshModelAndBind(onOver: Runnable?)

  fun shouldKeepOn(): Boolean

  fun isFirstLifeCycle(): Boolean

  fun isInteracting(): Boolean

  fun isAlive(): Boolean

  fun isRefreshingModelAndBinding(): Boolean

}