package com.hagergroup.sweetpotato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.hagergroup.sweetpotato.app.SweetActivityController;

/**
 * An empty interface which should be used as a marker on an {@link Activity}, which does not want to be requested by the
 * {@link SweetActivityController.Redirector}.
 * <p>
 * When an {@link Activity} implements this interface, the {@link SweetActivityController.Redirector#getRedirection(AppCompatActivity)} method will not be
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