package org.pkwmtt.moderator.controller;

import org.pkwmtt.exceptions.SpecifiedGeneralGroupDoesntExistsException;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice(assignableTypes = ModeratorController.class)
public class ModeratorControllerAdvice {

    @ExceptionHandler(SpecifiedGeneralGroupDoesntExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(SpecifiedGeneralGroupDoesntExistsException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(e.getMessage()));
    }

}
