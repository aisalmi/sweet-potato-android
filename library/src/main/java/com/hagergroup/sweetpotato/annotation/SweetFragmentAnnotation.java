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
import com.hagergroup.sweetpotato.lifecycle.DummySweetViewModel;
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel;

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
   * Defines the context which should be attached to the {@link SweetViewModel} holds by the {@link SweetFragment}.
   */
  enum ViewModelContext
  {
    /**
     * The {@link SweetViewModel} should be attached to the fragment
     */
    Fragment,

    /**
     * The {@link SweetViewModel} should be attached to the activity
     */
    Activity
  }

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
   * @return the {@link SweetViewModel} class to be created and held by the fragment.
   */
  Class<? extends SweetViewModel> viewModelClass() default DummySweetViewModel.class;

  /**
   * @return the layout identifier to be used in the
   * {@link SweetFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)} method.
   */
  @LayoutRes
  int layoutId();

  /**
   * @return true if databinding should be done a first time before setting the model
   */
  boolean preBind() default false;

  /**
   * @return the context to be attached to the {@link SweetViewModel}.
   */
  SweetFragmentAnnotation.ViewModelContext viewModelContext() default SweetFragmentAnnotation.ViewModelContext.Fragment;

}
