package pl.lukaszgrymulski.noteservice.advisor;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.lukaszgrymulski.noteservice.service.FieldValidationErrorService;

import java.util.Map;

@ControllerAdvice
@Slf4j(topic = "application.logger")
public class ControllerAdvisor {

    @ExceptionHandler(BindException.class)
    public ResponseEntity handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map bindingResultMapped = FieldValidationErrorService.getErrorMap(bindingResult);
        log.error("Error while binding: {}", bindingResultMapped);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(bindingResultMapped);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(NotFoundException e) {
        log.error("Not found exception: {}", e.getLocalizedMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getLocalizedMessage());
    }

}
