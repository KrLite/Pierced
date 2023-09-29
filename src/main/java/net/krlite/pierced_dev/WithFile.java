package net.krlite.pierced_dev;

import net.krlite.pierced_dev.annotation.Silent;

import java.io.File;

public abstract class WithFile extends WithExceptions {
    private @Silent final File file;

    protected WithFile(File file) {
        this.file = file;
    }

    protected File file() {
        return file;
    }
}
