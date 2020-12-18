package com.hagergroup.sweetpotato.lifecycle

/**
 * This exception should be triggered on the framework methods which allow to throw it, when a business object is not accessible.
 *
 * @author Ludovic Roland
 * @since 2018.11.06
 */
class ModelUnavailableException
  : Exception
{

  val code: Int

  constructor() : this(0)

  constructor(code: Int) : this(null, null, code)

  constructor(message: String, cause: Throwable) : this(message, cause, 0)

  constructor(message: String) : this(message, 0)

  constructor(message: String, code: Int) : this(message, null, code)

  constructor(cause: Throwable) : this(cause, 0)

  constructor(cause: Throwable, code: Int) : this(null, cause, code)

  constructor(message: String?, cause: Throwable?, code: Int) : super(message, cause)
  {
    this.code = code
  }

}
