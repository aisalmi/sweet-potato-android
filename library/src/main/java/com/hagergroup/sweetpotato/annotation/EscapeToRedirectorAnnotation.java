package com.hagergroup.sweetpotato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.appcompat.app.AppCompatActivity;

import com.hagergroup.sweetpotato.app.SweetActivityController;
import com.hagergroup.sweetpotato.appcompat.app.SweetAppCompatActivity;

/**
 * Annotation which should be used as a marker on a {@link SweetAppCompatActivity}, which does not want to be requested by the
 * {@link SweetActivityController.Redirector}.
 * <p>
 * When a {@link SweetAppCompatActivity} uses this annotation, the {@link SweetActivityController.Redirector#getRedirection(AppCompatActivity)} method will not be
 * invoked.
 * </p>
 *
 * @see SweetActivityController#needsRedirection(AppCompatActivity)
 *
 * @author Ludovic Roland
 * @since 2018.12.31
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EscapeToRedirectorAnnotation
{

}
