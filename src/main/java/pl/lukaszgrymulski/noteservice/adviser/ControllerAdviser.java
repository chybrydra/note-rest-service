package pl.lukaszgrymulski.noteservice.adviser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.lukaszgrymulski.noteservice.exception.RecordNotFoundException;
import pl.lukaszgrymulski.noteservice.service.FieldValidationErrorService;
import pl.lukaszgrymulski.noteservice.utils.ApiError;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j(topic = "application.logger")
public class ControllerAdviser {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> handleBindException(HttpServletRequest request, BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String bindingResultString = FieldValidationErrorService.getAsOneLineString(bindingResult);

        log.error("Error while binding: {}", bindingResultString);
        ApiError apiError = ApiError.status(HttpStatus.BAD_REQUEST)
                .path(request)
                .message("Binding exception.")
                .debugMessage(bindingResultString)
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(HttpServletRequest request, RecordNotFoundException e) {
        log.error("Not found exception: {}", e.getLocalizedMessage());
        ApiError error = ApiError.status(HttpStatus.NOT_FOUND)
                        .message(e.getLocalizedMessage())
                        .path(request)
                        .debugMessage(e)
                        .build();
        return ResponseEntity
                .status(error.getStatus())
                .body(error);
    }

}
