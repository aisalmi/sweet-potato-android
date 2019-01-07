package com.hagergroup.sweetpotato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.appcompat.app.ActionBar;

import com.hagergroup.sweetpotato.appcompat.app.SweetActivityAggregate;
import com.hagergroup.sweetpotato.appcompat.app.SweetAppCompatActivity;

/**
 * Annotation which should be used on a {@link SweetAppCompatActivity}, in order to configure the {@link ActionBar} behavior.
 * <p>
 * When a {@link SweetAppCompatActivity} uses this interface, the associated {@link SweetActivityAggregate} will configure the behavior of the {@link ActionBar}.
 * </p>
 *
 * @author Ludovic Roland
 * @see SweetActivityAggregate
 * @since 2018.11.09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SweetActionBarAnnotation
{

  /**
   * Defines the available {@link ActionBar} "home" button action behaviors handled by the {@link SweetActivityAggregate}.
   */
  enum ActionBarBehavior
  {
    /**
     * The {@link ActionBar} will display an "up" arrow icon
     */
    Up,

    /**
     * The {@link ActionBar} will display a hamburger icon
     */
    Drawer,

    /**
     * The {@link ActionBar} will display nothing
     */
    None
  }

  /**
   * @return the behavior to be applied to the {@link ActionBar}.
   */
  ActionBarBehavior actionBarBehavior() default ActionBarBehavior.None;

}