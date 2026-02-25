package consome.interfaces.advice;

import consome.domain.comment.exception.CommentException;
import consome.domain.common.exception.BusinessException;
import consome.domain.message.exception.MessageException;
import consome.domain.post.exception.PostException;
import consome.domain.report.exception.ReportException;
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

    // ===== User Exceptions =====
    @ExceptionHandler(UserException.DuplicateLoginId.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLoginId(UserException.DuplicateLoginId ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(UserException.loginFailure.class)
    public ResponseEntity<ErrorResponse> handleLoginFailure(UserException.loginFailure ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {
        HttpStatus status = switch (ex.getCode()) {
            case "USER_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "PASSWORD_MISMATCH", "INSUFFICIENT_POINT" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    // ===== Post Exceptions =====
    @ExceptionHandler(PostException.NotFound.class)
    public ResponseEntity<ErrorResponse> handlePostNotFound(PostException.NotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(PostException.Unauthorized.class)
    public ResponseEntity<ErrorResponse> handlePostUnauthorized(PostException.Unauthorized ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorResponse> handlePostException(PostException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    // ===== Comment Exceptions =====
    @ExceptionHandler(CommentException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(CommentException.NotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(CommentException.Unauthorized.class)
    public ResponseEntity<ErrorResponse> handleCommentUnauthorized(CommentException.Unauthorized ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ErrorResponse> handleCommentException(CommentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    // ===== Message Exceptions =====
    @ExceptionHandler(MessageException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleMessageNotFound(MessageException.NotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MessageException.AccessDenied.class)
    public ResponseEntity<ErrorResponse> handleMessageAccessDenied(MessageException.AccessDenied ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorResponse> handleMessageException(MessageException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    // ===== Report Exceptions =====
    @ExceptionHandler(ReportException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleReportNotFound(ReportException.NotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(ReportException.class)
    public ResponseEntity<ErrorResponse> handleReportException(ReportException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    // ===== Business Exceptions =====
    @ExceptionHandler(BusinessException.BoardNotFound.class)
    public ResponseEntity<ErrorResponse> handleBoardNotFound(BusinessException.BoardNotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.CategoryNotFound.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(BusinessException.CategoryNotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    // ===== Fallback for Illegal* Exceptions (하위호환) =====
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("code", "INVALID_ARGUMENT");
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("code", "INVALID_STATE");
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    // ===== Validation Exceptions =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        Map<String, String> body = new HashMap<>();
        body.put("code", "INVALID_INPUT");
        body.put("message", message);

        return ResponseEntity.badRequest().body(body);
    }
}
