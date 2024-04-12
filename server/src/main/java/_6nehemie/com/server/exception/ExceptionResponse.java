package _6nehemie.com.server.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionResponse {

    private Long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ExceptionResponse() {
    }
}
