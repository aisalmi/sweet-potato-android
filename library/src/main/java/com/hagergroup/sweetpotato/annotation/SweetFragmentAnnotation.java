package com.hagergroup.sweetpotato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SweetFragmentAnnotation
{

  @StringRes
  int fragmentTitleId() default -1;

  @StringRes
  int fragmentSubTitleId() default -1;

  @LayoutRes
  int layoutId();

  boolean surviveOnConfigurationChanged() default false;

}
