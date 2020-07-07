package dfki.com.smartmaas.feedbackservice.exception;

public class InvalidLocationNameException extends Exception {
    public InvalidLocationNameException(final String message) {
        super(message);
    }

    public InvalidLocationNameException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
