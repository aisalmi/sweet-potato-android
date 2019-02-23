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

  /**
   * This is the place where the implementing class can initialize the previously retrieved graphical objects. This method is invoked only once during
   * the entity life cycle.
   * <p>
   * This method will be invoked just after the [onRetrieveModel] method first invocation has successfully returned.
   * </p>
   * <p>
   * It is ensured that this method will be invoked from the UI thread!
   * </p>
   * <p>
   * Never invoke this method, only the framework should, because this is a callback!
   * </p>
   */
  fun onBindModel()

  /**
   * Asks the implementing entity to reload its business objects and to synchronize its display. The method invokes the
   * [onRetrieveModel] method.
   *
   * @param retrieveModel indicates whether the [onRetrieveModel]  method should be invoked or not
   * @param onOver                  if not `null`, this method will be eventually invoked from the UI thread
   * @param immediately             if this flag is set to `true`, even if the implementing entity is not currently displayed, the execution will be run at once. If
   *                                set to `false`, the execution will be delayed until the entity [AppCompatActivity.onResume] or [Fragment.onResume]
   *                                method is invoked
   */
  fun refreshModelAndBind(retrieveModel: Boolean, onOver: Runnable?, immediately: Boolean)

  /**
   * Indicates whether nothing went wrong during the implementing entity, or if a redirection is being processed.
   * <p>
   * Thanks to the [com.hagergroup.sweetpotato.app.SweetActivityController.Redirector], it is possible to re-route any [com.hagergroup.sweetpotato.app.Sweetable][AppCompatActivity]
   * when it starts, if some other [AppCompatActivity] needs to be executed beforehand.
   * </p>
   *
   * @return `true` if and only if the implementing [AppCompatActivity]/[Fragment] entity should resume its execution
   */
  fun shouldKeepOn(): Boolean

  /**
   * Provides information about the current entity life cycle.
   *
   * @return `true` if and only if the entity life cycle is the first time to execute during its container life
   */
  fun isFirstLifeCycle(): Boolean

  /**
   * Provides information about the current entity life cycle.
   * <p>
   * It is very handy when it comes to know whether the end-user can interact with the underlying [AppCompatActivity]/[Fragment] entity.
   * </p>
   *
   * @return `true` if and only if the underlying [AppCompatActivity]/[Fragment] entity life-cycle is between the
   * [AppCompatActivity.onResume]/[Fragment.onResume] and [AppCompatActivity.onPause]/[Fragment.onPause] methods
   */
  fun isInteracting(): Boolean

  /**
   * Indicates whether the current entity is still alive.
   * <p>
   * It enables to know whether the underlying [AppCompatActivity]/[Fragment] entity UI is still available for being handled.
   * </p>
   *
   * @return `true` if and only if the underlying [AppCompatActivity]/[Fragment] entity [AppCompatActivity.onDestroy]/[Fragment.onDestroy]
   * method has already been invoked
   */
  fun isAlive(): Boolean

  /**
   * Indicates whether the extending [AppCompatActivity]/[Fragment] entity implementing the [SweetLifeCycle] interface is in
   * the middle of a [refreshModelAndBind] call.
   * <p>
   * It is very handy when it comes to disable certain things, like menu entries, while an [AppCompatActivity]/[Fragment] is loading.
   * </p>
   *
   * @return `true` if and only if the [refreshModelAndBind] is being executed.
   */
  fun isRefreshingModelAndBinding(): Boolean

}