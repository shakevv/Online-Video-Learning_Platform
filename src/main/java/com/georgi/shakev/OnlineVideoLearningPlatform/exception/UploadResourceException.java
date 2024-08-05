package com.georgi.shakev.OnlineVideoLearningPlatform.exception;

public class UploadResourceException extends RuntimeException {
    public UploadResourceException() {
    }

    public UploadResourceException(String message) {
        super(message);
    }

    public UploadResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadResourceException(Throwable cause) {
        super(cause);
    }
}
