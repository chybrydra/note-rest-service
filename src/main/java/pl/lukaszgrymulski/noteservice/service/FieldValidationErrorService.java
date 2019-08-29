package pl.lukaszgrymulski.noteservice.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class FieldValidationErrorService {

    private FieldValidationErrorService() {}

    public static String getAsOneLineString(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder("");
        for (FieldError error : bindingResult.getFieldErrors()) {
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
