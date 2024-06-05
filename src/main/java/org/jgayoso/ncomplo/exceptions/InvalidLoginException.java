package org.jgayoso.ncomplo.exceptions;

public class InvalidLoginException extends Exception {

  public InvalidLoginException(final String login) {
    super("Invalid login " + login);
  }
}
