package net.krlite.pierced_dev.ast.io;

import net.krlite.pierced.io.toml.TomlRegex;
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

    private void init() {
        if (!file().exists()) {
            try {
                file().getParentFile().mkdirs();
                file().createNewFile();
            } catch (IOException e) {
                addException(e);
            }
        } else {
            try {
                FileWriter writer = new FileWriter(file());
                writeAndClose(writer, "");
            } catch (IOException e) {
                addException(e);
            }
        }
    }

    private boolean separate = false, ready = false;

    private void write(String line) throws IOException {
        FileWriter writer = new FileWriter(file(), true);
        Matcher commentMatcher = Comment.COMMENT.matcher(line);
        boolean commentMatched = commentMatcher.matches();

        if (ready) {
            writer.write(Util.NEWLINE);
            if ((separate && !commentMatched && !line.isEmpty()))
                writer.write(Util.NEWLINE);
        } else ready = true;

        separate = commentMatched;
        writeAndClose(writer, line);
    }

    private void write(String key, String value) {
        try {
            write(key + " = " + value);
        } catch (IOException e) {
            addException(e);
        }
    }

    private void writeAndClose(java.io.Writer writer, String content) throws IOException {
        writer.write(content);
        writer.flush();
        writer.close();
    }
}
