package org.pkwmtt.calendar.exams.controllers;

import jakarta.validation.ConstraintViolationException;
import org.pkwmtt.exceptions.*;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = {ExamController.class})
public class ExamControllerAdvice {

    @ExceptionHandler(NoSuchElementWithProvidedIdException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoSuchElementWithProvidedIdException(NoSuchElementWithProvidedIdException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler({
            ExamTypeNotExistsException.class,
            InvalidGroupIdentifierException.class,
            SpecifiedGeneralGroupDoesntExistsException.class,
            SpecifiedSubGroupDoesntExistsException.class,
            UnsupportedCountOfArgumentsException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(field -> field.getField() + " : " + field.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(field -> field.getPropertyPath() + " : " + field.getMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(message));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflicts(RuntimeException e) {
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(e.getMessage()));
    }
}
