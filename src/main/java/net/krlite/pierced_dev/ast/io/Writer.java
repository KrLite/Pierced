package net.krlite.pierced_dev.ast.io;

import net.krlite.pierced_dev.ExceptionHandler;
import net.krlite.pierced_dev.WithFile;
import net.krlite.pierced_dev.ast.regex.Comment;
import net.krlite.pierced_dev.ast.util.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;

public class Writer extends WithFile {
    public Writer(File file) {
        super(file);
    }

    @Override
    public File file() {
        return super.file();
    }

    public void init() {
        if (!file().exists()) {
            try {
                // Create file
                file().getParentFile().mkdirs();
                file().createNewFile();
            } catch (IOException e) {
                addException(e);
            }
        } else {
            try {
                // Clear content
                FileWriter writer = new FileWriter(file(), false);
                writeAndClose(writer, "");
            } catch (IOException e) {
                addException(e);
            }
        }
    }

    private void write(String key, String value) {
        write(Util.formatLine(key, value));
    }

    private void write(String line) {
        FileWriter writer;
        try {
            writer = new FileWriter(file(), true);
        } catch (IOException e) {
            addException(ExceptionHandler.handleFileWriterCreateException(e));
            return;
        }

        Matcher commentMatcher = Comment.COMMENT.matcher(line);
        boolean commentMatched = commentMatcher.matches();

        writeNewLinesAroundComment(writer, !commentMatched && !line.isEmpty());

        commentShouldAppendNewLine = commentMatched;
        writeAndClose(writer, line);
    }

    private boolean commentShouldAppendNewLine = false, commentShouldPrependNewLine = false;

    private void writeNewLinesAroundComment(java.io.Writer writer, boolean shouldAppendNewLine) {
        if (commentShouldPrependNewLine) {
            writeNewLine(writer);

            if ((commentShouldAppendNewLine && shouldAppendNewLine))
                writeNewLine(writer);

        } else commentShouldPrependNewLine = true;
    }

    private void writeNewLine(java.io.Writer writer) {
        try {
            writer.write(Util.LINE_TERMINATOR);
        } catch (IOException e) {
            addException(ExceptionHandler.handleFileWriterWriteException(e));
        }
    }

    private void writeAndClose(java.io.Writer writer, String content) {
        try {
            writer.write(content);
        } catch (IOException e) {
            addException(ExceptionHandler.handleFileWriterWriteException(e));
        }

        try {
            writer.flush();
        } catch (IOException e) {
            addException(ExceptionHandler.handleFileWriterFlushException(e));
        }

        try {
            writer.close();
        } catch (IOException e) {
            addException(ExceptionHandler.handleFileWriterCloseException(e));
        }
    }
}
