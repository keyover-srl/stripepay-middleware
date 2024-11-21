package it.keyover.stripepay.exception;

/**
 *
 * @author red
 */

import com.stripe.exception.StripeException;

public class CustomStripeException extends StripeException {
  private static final long serialVersionUID = 2L;

  public CustomStripeException(String message) {
    this(message, null);
  }

  public CustomStripeException(String message, Throwable e) {
    super(message, null, null, 0, e);
  }
}