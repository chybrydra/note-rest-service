package pl.lukaszgrymulski.noteservice.advisor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.lukaszgrymulski.noteservice.service.FieldValidationErrorService;

import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler({BindException.class})
    public ResponseEntity handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map bindingResultMapped = FieldValidationErrorService.getErrorMap(bindingResult);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(bindingResultMapped);
    }

}
