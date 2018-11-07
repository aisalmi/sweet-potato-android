package com.hagergroup.sweetpotato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import com.hagergroup.sweetpotato.fragment.app.DummySweetFragment;
import com.hagergroup.sweetpotato.fragment.app.SweetFragment;

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SweetActivityAnnotation
{

  @LayoutRes
  int contentViewId();

  @IdRes
  int fragmentPlaceholderId() default -1;

  Class<? extends SweetFragment<?>> fragmentClass() default DummySweetFragment.class;

  boolean addFragmentToBackStack() default false;

  String fragmentBackStackName() default "";

  boolean canRotate() default false;

}