package com.hagergroup.sweetpotato.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hagergroup.sweetpotato.appcompat.app.SweetAppCompatActivity;
import com.hagergroup.sweetpotato.content.SweetBroadcastListener;
import com.hagergroup.sweetpotato.content.SweetBroadcastListenerProvider;
import com.hagergroup.sweetpotato.fragment.app.SweetFragment;

/**
 * When an {@link SweetAppCompatActivity} or a {@link SweetFragment} implements that interface, it will send broadcast intents while loading and once the loading is over.
 *
 * @see SweetBroadcastListener
 * @see SweetBroadcastListenerProvider
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SweetSendLoadingIntentAnnotation
{

}
