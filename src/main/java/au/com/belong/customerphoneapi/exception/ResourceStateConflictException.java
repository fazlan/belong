package au.com.belong.customerphoneapi.exception;

public class ResourceStateConflictException extends RuntimeException {

    public ResourceStateConflictException(String message) {
        super(message);
    }
}
