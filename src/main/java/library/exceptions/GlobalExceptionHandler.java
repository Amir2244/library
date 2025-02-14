package library.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.CONFLICT, ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors),
                HttpStatus.BAD_REQUEST
        );
    }
}