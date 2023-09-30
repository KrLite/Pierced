package net.krlite.pierced_dev;

import java.io.IOException;

public class ExceptionHandler {
    public static Exception handleSetFieldException(Throwable e, String fieldName) {
        return new RuntimeException("Cannot set field '" + fieldName + "'!", e);
    }

    public static Exception handleFindFieldException(Throwable e, String fieldName) {
        return new RuntimeException("Cannot find field '" + fieldName + "'!", e);
    }

    public static Exception handleFieldDoesNotExistException(Throwable e, String fieldName) {
        return new RuntimeException("Field '" + fieldName + "' does not exist!", e);
    }

    public static Exception handleFileDoesNotExistException(Throwable e, String fileName) {
        return new IOException("File '" + fileName + "' does not exist!", e);
    }

    public static Exception handleBufferReaderReadLineException(Throwable e) {
        return new IOException("Cannot read line!", e);
    }

    public static Exception handleBufferReaderCloseException(Throwable e) {
        return new IOException("Cannot close buffer reader!", e);
    }

    public static Exception handleFileWriterCreateException(Throwable e) {
        return new IOException("Cannot create buffer reader!", e);
    }

    public static Exception handleFileWriterWriteException(Throwable e) {
        return new IOException("Cannot write line!", e);
    }

    public static Exception handleFileWriterFlushException(Throwable e) {
        return new IOException("Cannot flush file writer!", e);
    }

    public static Exception handleFileWriterCloseException(Throwable e) {
        return new IOException("Cannot close file writer!", e);
    }

    public static Exception handleFieldIllegalAccessException(Throwable e, String fieldName) {
        return new RuntimeException("Cannot access field '" + fieldName + "'!", e);
    }
}
