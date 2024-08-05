package com.georgi.shakev.OnlineVideoLearningPlatform.controller;

import com.georgi.shakev.OnlineVideoLearningPlatform.exception.ResourceNotFoundException;
import com.georgi.shakev.OnlineVideoLearningPlatform.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String SERVER_ERROR = "Server error";
    private static final String ERROR_MSG_ATTRIBUTE = "errorMessage";

    private static String handleException(RuntimeException e, Model model) {
        String errorMessage = (e != null ? e.getMessage() : SERVER_ERROR);
        model.addAttribute(ERROR_MSG_ATTRIBUTE, errorMessage);
        assert e != null;
        log.error("Exception of type {} during execution with clause:{}, message: {}, stacktrace: {}",
                e.getClass(), e.getCause(), e.getMessage(), e.getStackTrace());
        return "error";
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException e, final Model model) {
        return handleException(e, model);
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleUserAlreadyExistsException(UserAlreadyExistsException e, final Model model) {
        return handleException(e, model);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(final RuntimeException e, final Model model) {
        return handleException(e, model);
    }
}
