package az.library.exception;

public class BorrowLimitExceededException extends RuntimeException {
    public BorrowLimitExceededException(String msg) {
        super(msg);
    }
}