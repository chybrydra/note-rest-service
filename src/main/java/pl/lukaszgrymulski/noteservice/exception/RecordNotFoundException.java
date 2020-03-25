package pl.lukaszgrymulski.noteservice.exception;

public class RecordNotFoundException extends Exception {

    public RecordNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}