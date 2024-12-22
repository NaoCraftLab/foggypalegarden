package com.naocraftlab.foggypalegarden.exception;

public abstract class FoggyPaleGardenException extends RuntimeException {

    public FoggyPaleGardenException(String message) {
        super(message);
    }

    public FoggyPaleGardenException(String message, Throwable cause) {
        super(message, cause);
    }
}
