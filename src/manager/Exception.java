package manager;

public class Exception extends RuntimeException {
    public Exception(String message) {
        super(message);
    }

    public class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }
}

