package com.example.smarthome.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

/**
 * Global exception handler for the smart home API.
 *
 * Intercepts exceptions thrown by controllers and translates them into
 * standardized HTTP error responses using {@link ProblemDetail}.
 *
 * Provides centralized handling for application-specific exceptions and
 * ensures consistent error structure across all API endpoints.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles cases where a requested device cannot be found.
     *
     * @param exception the thrown exception
     * @return a {@link ProblemDetail} response describing the error
     */
    @ExceptionHandler(DeviceNotFoundException.class)
    public ProblemDetail handleDeviceNotFound(DeviceNotFoundException exception){

        // Create the ProblemDetail
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "A device for this ID could not be found. Either it doesn't exist or it has been deleted."
        );

        problem.setType(URI.create("https://example.com/problems/device-not-found"));
        problem.setTitle("Device Not Found");

        // Log the exception
        logger.error("Internal Error Detected: " + exception.getMessage());

        problem.setInstance(null);

        return problem;
    }

    /**
     * Handles attempts to create a thermostat in a location that already has one.
     *
     * @param exception the thrown exception
     * @return a {@link ProblemDetail} response describing the error
     */
    @ExceptionHandler(ThermostatAlreadyExistsException.class)
    public ProblemDetail handleInvalidStatechartTransition(ThermostatAlreadyExistsException exception){

        // Create the ProblemDetail
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "The provided location already has a thermostat"
        );

        problem.setType(URI.create("https://example.com/problems/theromstat-already-exists"));
        problem.setTitle("Thermostat Already Exists");

        // Log the exception
        logger.error("Internal Error Detected: " + exception.getMessage());

        problem.setInstance(null);

        return problem;
    }

    /**
     * Handles operations that require devices when none exist in the system.
     *
     * @param exception the thrown exception
     * @return a {@link ProblemDetail} response describing the error
     */
    @ExceptionHandler(NoDevicesException.class)
    public ProblemDetail handleNoDevicesExceptions(NoDevicesException exception){

        // Create the ProblemDetail
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "There are no devices in the smart home. Action cannot be performed"
        );

        problem.setType(URI.create("https://example.com/problems/no-devices"));
        problem.setTitle("No Devices in Smart Home");

        // Log the exception
        logger.error("Internal Error Detected: " + exception.getMessage());

        problem.setInstance(null);

        return problem;
    }

    /**
     * Handles all uncaught exceptions.
     *
     * @param exception the thrown exception
     * @return a generic {@link ProblemDetail} response for unexpected errors
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralExceptions(Exception exception){

        // Create the ProblemDetail
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An error occurred on our end."
        );

        // Log the exception
        logger.error("Unhandled Error: ", exception);

        problem.setTitle("Unexpected Error");
        problem.setType(URI.create("https://example.com/problems/general-problems"));

        problem.setInstance(null);

        return problem;

    }
}
