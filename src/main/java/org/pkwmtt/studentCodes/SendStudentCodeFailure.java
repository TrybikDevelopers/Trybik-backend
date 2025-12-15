package org.pkwmtt.studentCodes;

/**
 * Immutable DTO that represents a failure occurred while sending an OTP code to a group.
 * Contains the group identifier, a human-readable message and the exception class name
 * (useful for diagnostics without serializing full exception stack traces).
 *
 * @param superiorGroupName The name of the superior group for which sending OTP failed.
 * @param reason            Short, single-line reason for the failure (safe for display).
 * @param exceptionClass    Simple name of the exception class that was thrown (e.g. MailCouldNotBeSendException).
 */
public record SendStudentCodeFailure(String superiorGroupName, String reason, String exceptionClass) {
}

