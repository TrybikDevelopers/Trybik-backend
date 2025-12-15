package org.pkwmtt.exceptions;

public class WrongStudentCodeFormatException
  extends IllegalArgumentException {
    public WrongStudentCodeFormatException (String message) {
        super(message);
    }
}
