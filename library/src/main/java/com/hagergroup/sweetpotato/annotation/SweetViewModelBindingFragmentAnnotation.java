package com.hagergroup.sweetpotato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import com.hagergroup.sweetpotato.lifecycle.DummySweetViewModel;
import com.hagergroup.sweetpotato.lifecycle.SweetViewModel;

/**
 * @author Ludovic Roland
 * @since 2018.11.07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SweetViewModelBindingFragmentAnnotation
{

  @StringRes
  int fragmentTitleId() default -1;

  @StringRes
  int fragmentSubtitleId() default -1;

  Class<? extends SweetViewModel> viewModelClass() default DummySweetViewModel.class;

  @LayoutRes
  int layoutId();

  boolean surviveOnConfigurationChanged() default false;

}
