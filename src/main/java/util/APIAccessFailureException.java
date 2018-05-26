package util;

public class APIAccessFailureException extends RuntimeException {

    public APIAccessFailureException(String msg) {
        super(msg);
    }

    public APIAccessFailureException() {
        super();
    }

}
