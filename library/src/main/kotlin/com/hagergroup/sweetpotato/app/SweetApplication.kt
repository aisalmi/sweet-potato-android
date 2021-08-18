package com.hagergroup.sweetpotato.app

import android.app.Application
import android.content.res.Resources
import androidx.annotation.StringRes
import com.hagergroup.sweetpotato.R
import com.hagergroup.sweetpotato.exception.ExceptionHandlers
import com.hagergroup.sweetpotato.exception.SweetExceptionHandler
import com.hagergroup.sweetpotato.exception.SweetIssueAnalyzer
import com.hagergroup.sweetpotato.exception.SweetUncaughtExceptionHandler
import timber.log.Timber

/**
 * An abstract [Application] to be implemented when using the framework, because it initializes some of the components, and eases the development.
 * <p>
 * If you use your own implementation, do not forget to declare it in the `AndroidManifest.xml` file.
 * </p>
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
abstract class SweetApplication<ApplicationConstantsClass : SweetApplication.ApplicationConstants>
  : Application()
{

  /**
   * Contains various attributes in order to have the default dialog boxes related to exceptions i18ned.
   *
   * @param businessObjectAvailabilityProblemHint the body of the error dialog box when the business objects are not available on an [androidx.appcompat.app.AppCompatActivity]
   * @param connectivityProblemHint               the body of the error dialog box a connectivity issue occurs
   * @param otherProblemHint                      the "Retry" button label of the dialog box when a connectivity issue occurs
   */
  data class I18N(@StringRes val businessObjectAvailabilityProblemHint: Int,
                  @StringRes val connectivityProblemHint: Int,
                  @StringRes val otherProblemHint: Int)

  /**
   * Contains various attributes in order to have some context of the app environment.
   */
  open class ApplicationConstants(resources: Resources)
  {

    val isSmartphone: Boolean = resources.getBoolean(R.bool.isSmartphone)

    val isPhablet: Boolean = resources.getBoolean(R.bool.isPhablet)

    val isTablet: Boolean = resources.getBoolean(R.bool.isTablet)

    val canRotate: Boolean = resources.getBoolean(R.bool.canRotate)

  }

  companion object
  {

    /**
     * A flag which enables to remember when the @link {@link Application#onCreate()} method invocation is over.
     */
    var isOnCreatedDone = false
      private set

    private lateinit var applicationConstants: ApplicationConstants

    fun <ApplicationConstantClass : ApplicationConstants> getApplicationConstants(): ApplicationConstantClass
    {
      return applicationConstants as ApplicationConstantClass
    }

  }

  var connectivityListener: SweetConnectivityListener? = null

  /**
   * Indicates whether the [onCreate] method has already been invoked.
   */
  private var onCreateInvoked = false

  /**
   * This method will be invoked by the [getExceptionHandler] method when building a [SweetExceptionHandler]
   * instance. This internationalization instance will be used to populate default dialog boxes texts popped-up by this default
   * [SweetExceptionHandler]. Hence, the method will be invoked at the application start-up.
   *
   * @return an instance which contains the internationalized text strings for some built-in error dialog boxes. You need to define
   * that method.
   */
  protected abstract fun getI18N(): SweetApplication.I18N

  /**
   * This method will be invoked just once during the [onCreate] method, in order to set [Timber].
   */
  protected abstract fun setupTimber()

  /**
   * The method will invoke the [onCreateCustom] method, provided the [shouldBeSilent] returns `false`, and will perform the following things:
   * <ol>
   * <li>setup [Timber],</li>
   * <li>register the [SweetExceptionHandler],</li>
   * <li>register the [SweetActivityController.Redirector],</li>
   * <li>register the [SweetActivityController.Interceptor],</li>
   * <li>register an internal default [Thread.UncaughtExceptionHandler] for the UI and for the background threads,</li>
   * <li>logs how much time the [onCreate] method execution took.</li>
   * </ol>
   * <p>
   * Note: if the method has already been invoked once, the second time (because of a concurrent access), it will do nothing but output an error log.
   * This is the reason why the method has been declared as synchronized.
   * </p>
   * <p>
   * This method normally does not need to be override, if needed override rather the [onCreateCustom] method
   * </p>
   *
   * @see setupTimber
   * @see getExceptionHandler
   * @see getActivityRedirector
   * @see getInterceptor
   */
  @Synchronized
  override fun onCreate()
  {
    setupTimber()

    if (onCreateInvoked == true)
    {
      Timber.e("The 'Application.onCreate()' method has already been invoked!")
      return
    }

    onCreateInvoked = true

    try
    {
      val start = System.currentTimeMillis()

      if (shouldBeSilent() == true)
      {
        Timber.d("Application starting in silent mode")

        super.onCreate()

        onCreateCustomSilent()

        return
      }

      Timber.d("Application starting")

      super.onCreate()

      // We register the application exception handler as soon as possible, in order to be able to handle exceptions
      setupDefaultExceptionHandlers()

      // We register the Activity redirector
      getActivityRedirector()?.let {
        SweetActivityController.registerRedirector(it)
      }

      // We register the entities interceptor
      getInterceptor()?.let {
        SweetActivityController.registerInterceptor(it)
      }

      retrieveConnectivityListener()?.let {
        connectivityListener = it
      }

      retrieveApplicationConstants()?.let {
        applicationConstants = it
      }

      onCreateCustom()

      Timber.d("The application with package name '$packageName' has started in ${System.currentTimeMillis() - start} ms")
    }
    finally
    {
      SweetApplication.isOnCreatedDone = true
    }
  }

  /**
   * A callback method which enables to indicate whether the process newly created should use the default [SweetApplication] workflow.
   * <p>
   * It is useful when having multiple processes for the same application, and that some of the processes should not use the framework.
   * </p>
   *
   * @return `true` if and only if you want the framework to be ignored for the process. Returns `false` by default
   *
   * @see onCreateCustomSilent
   */
  protected open fun shouldBeSilent(): Boolean =
      false

  /**
   * This callback will be invoked by the application instance, in order to get a reference on the application [SweetActivityController.Redirector]:
   * this method is responsible for creating an implementation of this component interface. Override this method, in order to control the redirection
   * mechanism.
   * <p>
   * It is ensured that the framework will only call once this method (unless you explicitly invoke it, which you should not), during the
   * [onCreate] method execution.
   * </p>
   *
   * @return an instance which indicates how to redirect activities if necessary. If `null`, this means that no redirection is
   * handled. Returns `null` by default
   *
   * @see [SweetActivityController.registerRedirector]
   */
  protected open fun getActivityRedirector(): SweetActivityController.Redirector? =
      null

  /**
   * This callback will be invoked by the application instance, in order to get a reference on the application [SweetConnectivityListener]:
   * this method is responsible for creating an implementation of this component interface. Override this method, in order to listen the connectivity.
   * <p>
   * It is ensured that the framework will only call once this method (unless you explicitly invoke it, which you should not), during the
   * [onCreate] method execution.
   * </p>
   *
   * @return an instance which will listen for the network connectivity. If `null`, this means that no network changes is
   * handled. Returns `null` by default
   *
   * @see [SweetConnectivityListener]
   */
  protected open fun retrieveConnectivityListener(): SweetConnectivityListener? =
      null

  /**
   * This callback will be invoked by the application instance, in order to get a reference on the application [ApplicationConstants]:
   * this method is responsible for creating an instance of this very simple class. Override this method, in order to provide your own class.
   * <p>
   * It is ensured that the framework will only call once this method (unless you explicitly invoke it, which you should not), during the
   * [onCreate] method execution.
   * </p>
   *
   * @return an instance which will provide some constants. Returns an instance of [ApplicationConstants] by default
   *
   * @see [ApplicationConstants]
   */
  protected open fun retrieveApplicationConstants(): ApplicationConstants? =
      ApplicationConstants(resources)

  /**
   * This callback will be invoked by the application instance, in order to get a reference on the application [SweetActivityController.Interceptor]:
   * this method is responsible for creating an implementation of this component interface. Override this method, in order to control the interception
   * mechanism.
   * <p>
   * It is ensured that the framework will only call once this method (unless you explicitly invoke it, which you should not), during the
   * [onCreate] method execution.
   * </p>
   *
   * @return an instance which will be invoked on every  life-cycle event. If `null`, this means that no interception is
   * handled. Returns `null` by default
   *
   * @see [[SweetActivityController.registerInterceptor]
   */
  protected open fun getInterceptor(): SweetActivityController.Interceptor? =
      null

  /**
   * This callback will be invoked by the application instance, in order to get a reference on the application [SweetExceptionHandler]:
   * this method is responsible for creating an implementation of this component interface. Override this
   * method, in order to handle more specifically some application-specific exceptions.
   * <p>
   * It is ensured that the framework will only call once this method (unless you explicitly invoke it, which should not be the case), during the
   * [onCreate] method execution.
   * </p>
   *
   * @return an instance which will be invoked when an exception occurs during the application, provided the exception is handled by the framework ;
   * may be {@code null}, if no [SweetExceptionHandler] should be used by the application. Returns a new instance of [SweetExceptionHandler] by default
   *
   * @see [SweetActivityController.registerExceptionHandler]
   */
  protected open fun getExceptionHandler(): SweetExceptionHandler? =
      ExceptionHandlers.DefaultExceptionHandler(getI18N(), SweetIssueAnalyzer.DefaultIssueAnalyzer(this))

  /**
   * This is the place where to register other default exception like Crashlytics, etc.
   * The default implementation does nothing, and if overriden, this method should not invoke its `super` method.
   * <p>
   * It is ensured that the framework default exception handlers will be set-up after this method, and they will fallback to the already registered
   * default exception handlers.
   * </p>
   */
  protected open fun onSetupExceptionHandlers()
  {
  }

  /**
   * This method will be invoked if and only if the [shouldBeSilent] method has returned `true`.
   * <p>
   * This enables to execute some code, even if the application runs in silent mode.
   * </p>
   *
   * @see shouldBeSilent
   */
  protected open fun onCreateCustomSilent()
  {
  }

  /**
   * This method will be invoked at the end of the [onCreate] method, once the framework initialization is over. You can override this
   * method, which does nothing by default, in order to initialize your application specific variables, invoke some services.
   * <p>
   * Keep in mind that this method should complete very quickly, in order to prevent from hanging the GUI thread, and thus causing a bad end-user
   * experience, and a potential ANR.
   * </p>
   * <p>
   * The method does nothing, by default.
   * </p>
   */
  protected open fun onCreateCustom()
  {
  }

  private fun setupDefaultExceptionHandlers()
  {
    // We let the overidding application register its exception handlers
    onSetupExceptionHandlers()

    SweetActivityController.registerExceptionHandler(getExceptionHandler())

    // We make sure that all uncaught exceptions will be intercepted and handled
    val builtinUuncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    val uncaughtExceptionHandler = SweetUncaughtExceptionHandler(builtinUuncaughtExceptionHandler)

    Timber.d("The application with package name '$packageName' " + (if (builtinUuncaughtExceptionHandler == null) "does not have" else "has") + " a built-in default uncaught exception handler")
    Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)
  }

}
