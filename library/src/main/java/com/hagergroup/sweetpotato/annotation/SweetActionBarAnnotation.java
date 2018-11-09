package com.hagergroup.sweetpotato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ludovic Roland
 * @since 2018.11.09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SweetActionBarAnnotation
{

  enum ActionBarBehavior
  {
    Up, Drawer, None
  }

  ActionBarBehavior actionBarBehavior() default ActionBarBehavior.None;

}