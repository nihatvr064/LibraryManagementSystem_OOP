package az.library.exception;

public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(String msg) {
        super(msg);
    }
}