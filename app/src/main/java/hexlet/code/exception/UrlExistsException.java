package hexlet.code.exception;

public class UrlExistsException extends RuntimeException {
    public UrlExistsException() {
        super();
    }

    public UrlExistsException(String message) {
        super(message);
    }
}
