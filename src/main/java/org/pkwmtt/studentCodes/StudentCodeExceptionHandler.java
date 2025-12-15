package org.pkwmtt.studentCodes;


import com.mysql.cj.exceptions.WrongArgumentException;
import org.pkwmtt.exceptions.*;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.pkwmtt.moderator.controller.ModeratorController;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(2)
@RestControllerAdvice(assignableTypes = {ModeratorController.class})
public class StudentCodeExceptionHandler {
    @ExceptionHandler({StudentCodeNotFoundException.class, WrongStudentCodeFormatException.class, UserNotFoundException.class, WrongArgumentException.class, SpecifiedGeneralGroupDoesntExistsException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDTO> handleBadRequests (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UserAlreadyAssignedException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler({MailCouldNotBeSendException.class})
    public ResponseEntity<ErrorResponseDTO> handleServerErrors (Exception e) {
        return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
