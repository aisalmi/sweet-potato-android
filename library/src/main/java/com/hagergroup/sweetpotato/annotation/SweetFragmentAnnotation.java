package com.hagergroup.sweetpotato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;

import com.hagergroup.sweetpotato.fragment.app.SweetFragment;

/**
 * Annotation which should be used on a {@link SweetFragment}, in order to configure it.
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SweetFragmentAnnotation
{

  /**
   * @return the string identifier to be used on {@link ActionBar#setTitle(int)}.
   */
  @StringRes
  int fragmentTitleId() default -1;

  /**
   * @return the string identifier to be setted on {@link ActionBar#setSubtitle(int)}.
   */
  @StringRes
  int fragmentSubtitleId() default -1;

  /**
   * @return the layout identifier to be used in the
   * {@link SweetFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)} method.
   */
  @LayoutRes
  int layoutId();

  /**
   * @return true if the fragment should survive when the configuration changes
   */
  boolean surviveOnConfigurationChanged() default false;

}
