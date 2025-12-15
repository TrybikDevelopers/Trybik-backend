package org.pkwmtt.exceptions;

public class StudentCodeNotFoundException
  extends RuntimeException {
    public StudentCodeNotFoundException () {
        super("Student code not found.");
    }
}
