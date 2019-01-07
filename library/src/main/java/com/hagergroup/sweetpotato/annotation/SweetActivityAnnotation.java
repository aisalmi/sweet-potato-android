package com.hagergroup.sweetpotato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import com.hagergroup.sweetpotato.appcompat.app.SweetAppCompatActivity;
import com.hagergroup.sweetpotato.fragment.app.DummySweetFragment;
import com.hagergroup.sweetpotato.fragment.app.SweetFragment;

/**
 * Annotation which should be used on a {@link SweetAppCompatActivity}, in order to configure it.
 *
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SweetActivityAnnotation
{

  /**
   * @return the layout identifier to be used in the {@link SweetAppCompatActivity#setContentView(int)} method.
   */
  @LayoutRes
  int contentViewId();

  /**
   * @return the view identifier to be used to place the {@link SweetFragment} defined by the
   * {@link SweetActivityAnnotation#fragmentClass()} method.
   */
  @IdRes
  int fragmentPlaceholderId() default -1;

  /**
   * @return the {@link SweetFragment} class to be created and displayed in the {@link SweetActivityAnnotation#fragmentPlaceholderId()} view holder.
   */
  Class<? extends SweetFragment<?>> fragmentClass() default DummySweetFragment.class;

  /**
   * @return if the fragment referred into the {@link SweetActivityAnnotation#fragmentClass()} should be added to the backStack or not.
   */
  boolean addFragmentToBackStack() default false;

  /**
   * @return the name of the fragment if it is add to the backStack.
   */
  String fragmentBackStackName() default "";

  /**
   * @return true if the activity can rotate.
   */
  boolean canRotate() default false;

}