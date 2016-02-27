package nl.wouter0100.one2xs.exceptions;

public class AuthTokenException extends Exception {

    public AuthTokenException(String error) {
        super(error);
    }
}
