package net.krlite.pierced.ast.io;

import net.krlite.pierced.ExceptionHandler;
import net.krlite.pierced.WithFile;
import net.krlite.pierced.annotation.*;
import net.krlite.pierced.ast.regex.Comment;
import net.krlite.pierced.ast.regex.NewLine;
import net.krlite.pierced.ast.util.Util;
import net.krlite.pierced.serialization.base.Serializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.regex.Matcher;

public class Writer extends WithFile {
    private boolean isFirstLine = true, isLastLineComment = false, shouldInsertNewLine = false;

    public Writer(File file) {
        super(file);
    }

    @Override
    public File file() {
        return super.file();
    }

    public void init() {
        // Init file
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

        // Init properties
        isFirstLine = true;
        isLastLineComment = false;
    }

    public  <T> void set(String rawKey, Object value, Serializer.Wrapper<T> wrapper) {
        if (value == null) return;

        writeKeyValuePair(rawKey, wrapper.serializer().serialize((T) value), value.getClass());
    }

    private void writeKeyValuePair(String key, String value, Class<?> clazz) {
        if (!key.isEmpty())
            writeLine(Util.formatLine(key, value, clazz));
    }

    private void writeComments(String... comments) {
        if (Arrays.stream(comments)
                .allMatch(Util::isCommentEmpty)) return;

        Arrays.stream(comments).forEach(comment ->
                Arrays.stream(comment.split(NewLine.NEWLINE.pattern()))
                        .map(Util::formatComment)
                        .forEach(line -> {
                            if (line.isEmpty())
                                shouldInsertNewLine = true;
                            else writeLine(line);
                        })
        );
    }

    public void writeComments(AnnotatedElement element, boolean shouldAppendNewLine) {
        if (element.isAnnotationPresent(net.krlite.pierced.annotation.Comment.class))
            writeComments(element.getAnnotation(net.krlite.pierced.annotation.Comment.class).value());

        if (element.isAnnotationPresent(Comments.class))
            writeComments(Arrays.stream(element.getAnnotation(Comments.class).value())
                    .map(net.krlite.pierced.annotation.Comment::value)
                    .toArray(String[]::new));

        shouldInsertNewLine = shouldAppendNewLine;
    }

    public void writeTableComments(AnnotatedElement element, Table table) {
        TableComment[] tableComments = new TableComment[]{};

        if (element.isAnnotationPresent(TableComment.class))
            tableComments = new TableComment[]{element.getAnnotation(TableComment.class)};
        if (element.isAnnotationPresent(TableComments.class))
            tableComments = element.getAnnotation(TableComments.class).value();

        writeComments(Arrays.stream(tableComments)
                .filter(tableComment ->
                        Util.flatten(Util.unescape(Util.normalizeStdTable(tableComment.table())), true)
                                .equals(Util.flatten(Util.unescape(Util.normalizeStdTable(table.value())), true)))
                .map(TableComment::comment)
                .toArray(String[]::new));
    }

    public void writeInlineComment(AnnotatedElement element) {
        if (element.isAnnotationPresent(InlineComment.class))
            writeInline(" " + Util.formatComment(element.getAnnotation(InlineComment.class).value()
                    .replaceAll(NewLine.NEWLINE.pattern(), "")));
    }

    public void writeTable(Table table) {
        if (!table.value().isEmpty())
            writeLine(Util.formatStdTable(table.value()));
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

        writeSurroundingNewLines: {
            // Write new line if not first line
            if (!isFirstLine) writeNewLine(writer);

            // Insert new line
            if (shouldInsertNewLine) writeNewLine(writer);

            // Write new lines before standard tables
            {
                Matcher stdTableMatcher = net.krlite.pierced.ast.regex.key.Table.STD_TABLE.matcher(line);
                boolean stdTableMatched = stdTableMatcher.matches();

                if (!isFirstLine && !isLastLineComment && stdTableMatched) {
                    writeNewLine(writer);
                }
            }

            // Write new lines before comments
            {
                Matcher commentMatcher = Comment.COMMENT.matcher(line);
                boolean commentMatched = commentMatcher.matches();

                if (!isFirstLine && !isLastLineComment && commentMatched) {
                    writeNewLine(writer);
                }

                isLastLineComment = commentMatched;
            }

            isFirstLine = false;
            shouldInsertNewLine = false;
        }

        // Write line
        writeAndClose(writer, line);
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
