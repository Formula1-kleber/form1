package br.gov.sp.cps.fatec.exception;

public class AuthException extends RuntimeException {
    public enum Reason { INVALID_DATA, USERNAME_TAKEN, BAD_CREDENTIALS }

    private final Reason reason;

    public AuthException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason reason() { return reason; }
}
