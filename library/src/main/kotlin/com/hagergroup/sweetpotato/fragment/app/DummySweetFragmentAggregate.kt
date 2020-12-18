package com.hagergroup.sweetpotato.fragment.app

import androidx.annotation.RestrictTo

/**
 * An internal default [SweetFragmentAggregate] class used by the [DummySweetFragment] only.
 *
 * @author Ludovic Roland
 * @since 2018.11.12
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class DummySweetFragmentAggregate(fragment: SweetFragment<*, *, *>, fragmentConfigurable: SweetFragmentConfigurable?)
  : SweetFragmentAggregate(fragment, fragmentConfigurable)