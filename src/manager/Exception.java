package manager;

import java.io.IOException;

public class Exception extends RuntimeException {
    public Exception(String message) {
        super(message);
    }

    public class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }
    public static class ManagerException extends IOException {
        public ManagerException(String message) {
            super(message);
        }
    }
}

