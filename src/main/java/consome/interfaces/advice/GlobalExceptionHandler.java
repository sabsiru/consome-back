package consome.interfaces.advice;

import consome.domain.user.exception.UserException;
import consome.interfaces.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.DuplicateLoginId.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLoginId(UserException.DuplicateLoginId ex) {
        ErrorResponse body = new ErrorResponse(
                ex.getCode(),
                ex.getMessage()
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(body);
    }

    @ExceptionHandler(UserException.loginFailure.class)
    public ResponseEntity<ErrorResponse> handleLoginFailure(UserException.loginFailure ex) {
        ErrorResponse body = new ErrorResponse(
                ex.getCode(),
                ex.getMessage()
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        Map<String, String> body = new HashMap<>();
        body.put("code", "INVALID_INPUT");
        body.put("message", message);

        return ResponseEntity.badRequest().body(body);
    }
}
