package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalException;
import org.pkwmtt.exceptions.SpecifiedSubGroupDoesntExistsException;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.WebPageContentNotAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for exceptions thrown from {@code TimetableController}.
 * <p>
 * Maps specific exception types to HTTP response statuses and builds an
 * {@link org.pkwmtt.exceptions.dto.ErrorResponseDTO} payload for the client.
 */
@SuppressWarnings({"LoggingSimilarMessage", "StringConcatenationArgumentToLogCall"})
@Slf4j
@RestControllerAdvice(assignableTypes = {TimetableController.class})
public class TimetableExceptionHandler {
    /**
     * Handles {@link WebPageContentNotAvailableException} thrown when the timetable
     * source web page cannot be reached or its content is unavailable.
     * <p>
     * Returns HTTP 503 (Service Unavailable) with an {@link ErrorResponseDTO}
     * containing the exception message. The exception message is also logged at
     * error level.
     *
     * @param e the thrown WebPageContentNotAvailableException
     * @return a ResponseEntity containing ErrorResponseDTO and HTTP 503 status
     */
    @ExceptionHandler(WebPageContentNotAvailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponseDTO> handleWebPageContentNotAvailableException (
      WebPageContentNotAvailableException e) {
        log.error("SERVICE_UNAVAILABLE # " + e.getMessage());
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    /**
     * Handles {@link JsonProcessingException} which occurs during JSON parsing
     * or generation within the controller processing.
     * <p>
     * Returns HTTP 500 (Internal Server Error) with a generic {@link ErrorResponseDTO}
     * message "Json Processing Failed". The underlying exception message is logged
     * at error level for diagnostics.
     *
     * @param e the thrown JsonProcessingException
     * @return a ResponseEntity containing ErrorResponseDTO and HTTP 500 status
     */
    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleJsonProcessingException (Exception e) {
        log.error("INTERNAL_SERVER_ERROR # " + e.getMessage());
        return new ResponseEntity<>(
          new ErrorResponseDTO("Json Processing Failed"),
          HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    /**
     * Handles client-caused errors such as:
     * - {@link SpecifiedGeneralGroupDoesntExistsException}
     * - {@link SpecifiedSubGroupDoesntExistsException}
     * - {@link IllegalArgumentException}
     * <p>
     * Returns HTTP 400 (Bad Request) with an {@link ErrorResponseDTO} containing
     * the exception message to inform the client about invalid input or missing
     * requested entities.
     *
     * @param e the thrown exception (one of the handled types)
     * @return a ResponseEntity containing ErrorResponseDTO and HTTP 400 status
     */
    @ExceptionHandler({SpecifiedGeneralGroupDoesntExistsException.class, SpecifiedSubGroupDoesntExistsException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleSpecifiedGeneralGroupDoesntExistsException (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles {@link IllegalAccessException} which indicates an unexpected
     * access violation during processing.
     * <p>
     * Returns HTTP 500 (Internal Server Error) with an {@link ErrorResponseDTO}
     * containing the exception message. The exception is also logged at error level.
     *
     * @param e the thrown IllegalAccessException
     * @return a ResponseEntity containing ErrorResponseDTO and HTTP 500 status
     */
    @ExceptionHandler(IllegalAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleIllegalAccessException (IllegalAccessException e) {
        log.error("INTERNAL_SERVER_ERROR # " + e.getMessage());
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Handles {@link InternalException} for unexpected internal application errors.
     * Returns HTTP 500 (Internal Server Error) with an {@link ErrorResponseDTO}
     * containing the exception message. The exception is logged at error level.
     *
     * @param e the thrown InternalException
     * @return a ResponseEntity containing ErrorResponseDTO and HTTP 500 status
     */
    @ExceptionHandler(InternalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleInternalException (InternalException e) {
        log.error("INTERNAL_SERVER_ERROR # " + e.getMessage());
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}