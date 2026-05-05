package com.example.smarthome.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

import static org.springframework.validation.BindingResultUtils.getBindingResult;

/**
 * Global exception handler for the Smart Home API.
 *
 * <p>
 * This class intercepts exceptions thrown across the application and converts them
 * into standardized HTTP error responses compliant with RFC 9457 (Problem Details for HTTP APIs).
 * </p>
 *
 * <p>
 * All error responses follow a consistent structure using {@link ProblemDetail}, including:
 * </p>
 *
 * <ul>
 *   <li><b>type</b> – A URI identifying the problem category</li>
 *   <li><b>title</b> – A short human-readable summary of the error</li>
 *   <li><b>status</b> – The HTTP status code</li>
 *   <li><b>detail</b> – A detailed explanation of the error</li>
 *   <li><b>instance</b> – The specific request URI that triggered the error</li>
 * </ul>
 *
 * <p>
 * Each handler returns a {@link ResponseEntity} to ensure full control over HTTP status codes
 * and response metadata, while maintaining RFC 9457-compliant body structure.
 * </p>
 *
 * <p>
 * This ensures consistent, machine-readable error responses across all API endpoints.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles cases where a requested device cannot be found.
     *
     * <p>
     * This exception is thrown when a device ID does not exist in the system
     * or has been deleted. It maps to an RFC 9457 Problem Details response
     * with HTTP 404 (Not Found).
     * </p>
     *
     * @param exception the thrown {@link DeviceNotFoundException}
     * @param request the current HTTP request used to populate the problem instance URI
     * @return a RFC 9457-compliant {@link ProblemDetail} wrapped in a {@link ResponseEntity}
     */
    public ResponseEntity<ProblemDetail> handleDeviceNotFound(
            DeviceNotFoundException exception,
            HttpServletRequest request) {

        logger.error("DeviceNotFound: {}", exception.getMessage());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Device Not Found");
        problem.setDetail("A device for this ID could not be found.");
        problem.setType(URI.create("https://example.com/problems/device-not-found"));
        problem.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    /**
     * Handles attempts to create a thermostat in a location that already has one.
     *
     * <p>
     * This enforces a business rule that each location may only contain one thermostat.
     * Violations result in an HTTP 409 (Conflict) response.
     * </p>
     *
     * @param exception the thrown {@link ThermostatAlreadyExistsException}
     * @param request the current HTTP request
     * @return a RFC 9457 Problem Details response describing the conflict
     */
    @ExceptionHandler(ThermostatAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidStatechartTransition(ThermostatAlreadyExistsException exception,
                                                           HttpServletRequest request){

        logger.error("ThermostatAlreadyExists: {}", exception.getMessage());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Thermostat Already Exists");
        problem.setDetail("The provided location already has a thermostat.");
        problem.setType(URI.create("https://example.com/problems/thermostat-already-exists"));
        problem.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    /**
     * Handles operations attempted when no devices exist in the system.
     *
     * <p>
     * This occurs when actions or queries require at least one device but the system
     * contains none. Returns HTTP 404 (Not Found).
     * </p>
     *
     * @param exception the thrown {@link NoDevicesException}
     * @param request the current HTTP request
     * @return a RFC 9457 Problem Details response indicating missing resources
     */
    @ExceptionHandler(NoDevicesException.class)
    public ResponseEntity<ProblemDetail> handleNoDevices(
            NoDevicesException exception,
            HttpServletRequest request) {

        logger.error("NoDevices: {}", exception.getMessage());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("No Devices Found");
        problem.setDetail("There are no devices in the smart home.");
        problem.setType(URI.create("https://example.com/problems/no-devices"));
        problem.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    /**
     * Handles invalid state machine transitions for smart devices.
     *
     * <p>
     * This exception occurs when a requested transition is not valid for the device's
     * current state. It maps to HTTP 400 (Bad Request).
     * </p>
     *
     * @param exception the thrown {@link InvalidStateTransitionException}
     * @param request the current HTTP request
     * @return a RFC 9457 Problem Details response describing the invalid transition
     */
    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ProblemDetail> handleInvalidTransition(
            InvalidStateTransitionException exception,
            HttpServletRequest request) {

        logger.error("InvalidStateTransition: {}", exception.getMessage());

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid State Transition");
        problem.setDetail(exception.getMessage());
        problem.setType(URI.create("https://example.com/problems/invalid-state-transition"));
        problem.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.badRequest().body(problem);
    }

    /**
     * Overrides Spring Boot’s default validation exception handler for request body validation failures.
     *
     * <p>
     * This method overrides
     * {@code ResponseEntityExceptionHandler#handleMethodArgumentNotValid} from Spring MVC.
     * It is triggered automatically when a {@code @Valid} or Bean Validation constraint fails
     * during request body binding (e.g., @NotNull, @Size, @Email).
     * </p>
     *
     * <p>
     * Instead of using Spring’s default error response, this implementation returns a
     * standardized RFC 9457 Problem Details response with field-level validation feedback.
     * </p>
     *
     * @param exception the {@link MethodArgumentNotValidException} thrown by Spring during validation
     * @param request the current {@link WebRequest} associated with the request
     * @return a RFC 9457-compliant {@link ProblemDetail} response containing validation error details
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");

        logger.warn("Validation error: {}", message);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation Error");
        problem.setDetail(message);
        problem.setType(URI.create("https://example.com/problems/validation-error"));
        problem.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    /**
     * Handles all uncaught exceptions not explicitly mapped elsewhere.
     *
     * <p>
     * This is a fallback handler ensuring the API never returns unstructured errors.
     * It maps unexpected failures to HTTP 500 (Internal Server Error).
     * </p>
     *
     * @param exception the unexpected exception
     * @param request the current HTTP request
     * @return a generic RFC 9457 Problem Details response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(
            Exception exception,
            HttpServletRequest request) {

        logger.error("Unhandled exception", exception);

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Unexpected Error");
        problem.setDetail("An internal error occurred.");
        problem.setType(URI.create("https://example.com/problems/internal-error"));
        problem.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
