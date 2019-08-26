package pl.lukaszgrymulski.noteservice.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Service
public class FieldValidationErrorService {

    private FieldValidationErrorService() {}

    public static Map getErrorMap(BindingResult result) {
        Map<String, String> validationMap = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            validationMap.put(error.getField(), error.getDefaultMessage());
        }
        return validationMap;
    }

}
