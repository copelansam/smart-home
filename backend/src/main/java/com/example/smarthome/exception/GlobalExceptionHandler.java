package com.example.smarthome.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(DeviceNotFoundException.class)
    public ProblemDetail handleDeviceNotFound(DeviceNotFoundException exception){

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "A device for this ID could not be found. Either it doesn't exist or it has been deleted."
        );

        problem.setType(URI.create("https://example.com/problems/device-not-found"));
        problem.setTitle("Device Not Found");

        logger.error("Internal Error Detected: " + exception.getMessage());

        problem.setInstance(null);

        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralExceptions(Exception exception){

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An error occurred on our end."
        );

        logger.error("Unhandled Error: ", exception);

        problem.setTitle("Unexpected Error");
        problem.setType(URI.create("https://example.com/problems/general-problems"));

        problem.setInstance(null);

        return problem;

    }


}
