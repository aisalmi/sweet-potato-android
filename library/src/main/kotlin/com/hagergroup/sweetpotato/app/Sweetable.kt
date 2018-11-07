package com.hagergroup.sweetpotato.app

import com.hagergroup.sweetpotato.lifecycle.SweetLifeCycle

/**
 * @author Ludovic Roland
 * @since 2018.11.06
 */
interface Sweetable<AggregateClass>
  : Sweetened<AggregateClass>, SweetLifeCycle