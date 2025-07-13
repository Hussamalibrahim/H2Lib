package com.library.library.Exception.Validation;

public class RateLimitExceededException extends RuntimeException {

  public RateLimitExceededException(String message) {
    super("Rate Should Be Between 1 And 5");
  }
}