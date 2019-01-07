package com.hagergroup.sweetpotato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hagergroup.sweetpotato.app.SweetLoadingAndErrorInterceptor;
import com.hagergroup.sweetpotato.app.SweetLoadingAndErrorInterceptor.SweetLoadingErrorAndRetryAggregate;
import com.hagergroup.sweetpotato.fragment.app.SweetFragment;

/**
 * Annotation which should be used on a {@link SweetFragment}, in order to configure the loading and the error behaviors.
 * <p>
 * When a {@link SweetFragment} uses this interface, the {@link SweetLoadingAndErrorInterceptor} will handle and configure the loading and error behavior.
 * </p>
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SweetLoadingAndErrorAnnotation
{

  /**
   * @return true if the {@link SweetLoadingErrorAndRetryAggregate} should handle the loading and error states
   */
  boolean enabled() default true;

  /**
   * return true if the {@link SweetLoadingErrorAndRetryAggregate} should manage the laoding state
   */
  boolean loadingEnabled() default true;

}
