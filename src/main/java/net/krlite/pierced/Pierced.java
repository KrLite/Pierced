package net.krlite.pierced;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public abstract class Pierced {
    protected final File file;
    protected HashMap<String, Object> map;

    protected Pierced(File file) {
        this.file = file;
        this.map = new HashMap<>();
    }

    protected File file() {
        return file;
    }

    protected TomlWriter writer() {
        return new TomlWriter.Builder()
                .indentTablesBy(2)
                .build();
    }

    protected boolean createFile() {
        return file().mkdirs();
    }

    protected boolean deleteFile() {
        return file().delete();
    }

    protected boolean readFromFile() {
        if (file().exists()) {
            map = new HashMap<>(new Toml().read(file()).toMap());
            return true;
        } else return false;
    }

    protected boolean writeToFile() {
        try {
            createFile();
            writer().write(map, file());
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }
}
