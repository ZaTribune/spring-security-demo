package account.error;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;


@Slf4j
@RestControllerAdvice
public class Advisor extends ResponseEntityExceptionHandler {


    public String extractUri(WebRequest request){
        String s= request.getDescription(false);
        return s.substring(s.indexOf("=")+1);
    }

    @Override
    protected ResponseEntity<Object>
    handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                 HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ErrorResponse response=ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .path(extractUri(request))
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Object>
    handleException(ConstraintViolationException ex, WebRequest request) {

        ex.printStackTrace();
        ErrorResponse response=ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(extractUri(request))
                .build();

        return ResponseEntity.badRequest().body(response);
    }

//    @ExceptionHandler(value = Exception.class)
//    public ResponseEntity<Object>
//    handleGenericException(Exception ex, WebRequest request) {
//
//        ex.printStackTrace();
//        ErrorResponse response=ErrorResponse.builder()
//                .timestamp(LocalDateTime.now().toString())
//                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .error("Something went wrong!")
//                .message(ex.getMessage())
//                .path(extractUri(request))
//                .build();
//
//        return ResponseEntity.badRequest().body(response);
//    }
}
