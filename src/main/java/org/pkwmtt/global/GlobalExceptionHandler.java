package org.pkwmtt.global;

import org.apache.logging.log4j.util.InternalException;
import org.pkwmtt.exceptions.IncorrectApiKeyValue;
import org.pkwmtt.exceptions.MaxUsageForStudentCodeReachedException;
import org.pkwmtt.exceptions.MissingHeaderException;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IncorrectApiKeyValue.class)
    public ResponseEntity<ErrorResponseDTO> handleIncorrectApiKeyValue (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(MissingHeaderException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingHeaderException (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ErrorResponseDTO> handleInternalException (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(MaxUsageForStudentCodeReachedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUsageForStudentCodeReachedException (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.FORBIDDEN);
    }
}
