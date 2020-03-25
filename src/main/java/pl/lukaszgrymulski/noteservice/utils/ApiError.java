package pl.lukaszgrymulski.noteservice.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ApiError implements Serializable {

    private static final long serialVersionUID = -1901393856235704692L;

    private final HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss a")
    private LocalDateTime timestamp = LocalDateTime.now();

    private final String message;

    private final String debugMessage;

    private final String errorPath;

    public static ApiError.Builder status(HttpStatus status) {
        return new ApiError.Builder(status);
    }

    public static class Builder {
        private HttpStatus status;
        private String message;
        private String debugMessage;
        private String errorPath;

        public Builder(HttpStatus status) {
            this.status = status;
            this.message = "";
            this.debugMessage = "";
            this.errorPath = "";
        }

        public Builder message(String message){
            this.message = message;
            return this;
        }

        public Builder debugMessage(String debugMsg) {
            this.debugMessage = debugMsg;
            return this;
        }

        public Builder debugMessage(Throwable exception) {
            this.debugMessage = exception.getLocalizedMessage();
            return this;
        }

        public Builder path(String path) {
            this.errorPath = path;
            return this;
        }

        public Builder path(HttpServletRequest request) {
            this.errorPath = request.getRequestURL().toString();
            return this;
        }

        public ApiError build() {
            return new ApiError(this.status, this.message, this.debugMessage, this.errorPath);
        }
    }
}