package net.krlite.pierced_dev.ast.io;

import net.krlite.pierced_dev.ExceptionHandler;
import net.krlite.pierced_dev.WithFile;
import net.krlite.pierced_dev.annotation.Comments;
import net.krlite.pierced_dev.annotation.InlineComment;
import net.krlite.pierced_dev.annotation.Table;
import net.krlite.pierced_dev.ast.regex.Comment;
import net.krlite.pierced_dev.ast.regex.NewLine;
import net.krlite.pierced_dev.ast.util.Util;
import net.krlite.pierced_dev.serialization.base.Serializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
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

    public  <T> void set(String rawKey, Object value, Serializer<T> serializer) {
        if (value == null) return;

        writeKeyValuePair(rawKey, serializer.serialize((T) value), value.getClass());
    }

    public void writeTable(Table table) {
        if (!table.value().isEmpty())
            writeLine(Util.formatStdTable(table.value()));
    }

    public void writeInlineComment(InlineComment inlineComment) {
        writeInline(" " + Util.formatComment(inlineComment.value().replaceAll(NewLine.NEWLINE.pattern(), "")));
    }

    public void writeComment(net.krlite.pierced_dev.annotation.Comment comment) {
        Arrays.stream(comment.value()
                .replaceFirst("^" + NewLine.NEWLINE.pattern(), "")
                .split(NewLine.NEWLINE.pattern()))
                .map(Util::formatComment)
                .forEach(this::writeLine);
    }

    public void writeComments(Comments comments) {
        Arrays.stream(comments.value()).forEach(this::writeComment);
    }

    public void writeTypeComment(net.krlite.pierced_dev.annotation.Comment comment) {
        if (!Util.isCommentEmpty(comment.value()))
            writeComment(comment);
    }

    public void writeTypeComments(Comments comments) {
        if (!Arrays.stream(comments.value())
                .map(net.krlite.pierced_dev.annotation.Comment::value)
                .allMatch(Util::isCommentEmpty))
            writeComments(comments);
    }

    private void writeKeyValuePair(String key, String value, Class<?> clazz) {
        if (!key.isEmpty())
            writeLine(Util.formatLine(key, value, clazz));
    }

    private void writeInline(String inline) {
        FileWriter writer;
        try {
            writer = new FileWriter(file(), true);
        } catch (IOException e) {
            addException(ExceptionHandler.handleFileWriterCreateException(e));
            return;
        }

        writeAndClose(writer, inline);
    }

    private void writeLine(String line) {
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
