package org.kata;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <code>FileBasedStorage</code> is simple implementation of storage backed with
 * temporal file. Stored data is guaranteed to be available while JVM run.
 * Once JVM is stopped the temporal file might be removed (depending on OS
 * type and settings) and all data lost.
 *
 * @param <T> a type of objects stored in this storage
 */
public class FileBasedStorage<T> implements Storage<T> {
    private Logger LOG = Logger.getLogger(FileBasedStorage.class.getName());

    // @to-do: this maps needs to be stored in file, non in mem too
    private Map<Long, Long> offsetInFileByHandle = new HashMap<>();

    private FileInputStream fis;
    private FileOutputStream fos;

    private static long currentHandle = 0L;
    private long position = 0L;

    public FileBasedStorage() throws IOException {
        Path dataFile = Files.createTempFile(null, null);
        this.fis = new FileInputStream(dataFile.toFile());
        this.fos = new FileOutputStream(dataFile.toFile(), true);

        deleteDataFileOnExit(dataFile);
    }

    /**
     * Stores object in the storage
     * <p>Note: This implementation adds all stored data to the end of file.
     * It never deletes anything from the file. If you need to save new version
     * of object you will need to save it in the storage and get new handle</p>
     */
    @Override
    public long create(T object) throws IOException {
        long handle = nextHandle();
        offsetInFileByHandle.put(handle, appendObjectToDataFile(object));
        return handle;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public T load(long handle) throws IOException, ClassNotFoundException {
        long offset = offsetInFileByHandle.get(handle);
        fis.getChannel().position(offset);
        ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(fis));
        return (T) ois.readObject();
    }

    @Override
    public void update(T object, long handle) throws IOException {
        offsetInFileByHandle.put(handle, appendObjectToDataFile(object));
    }

    private long nextHandle() {
        return currentHandle++;
    }

    private long appendObjectToDataFile(T object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            position = fos.getChannel().position();
            bos.writeTo(fos);
            return position;
        }
    }

    private void deleteDataFileOnExit(Path dataFile) {
        dataFile.toFile().deleteOnExit();

        // The shutdown hook is required to close  I/O streams worked with
        // data file otherwise deleteOnExit would not remove temp data file
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                closeQueitely(fis);
                closeQueitely(fos);
            }
        });
    }

    private void closeQueitely(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOG.warning("Unable to close IO stream");
            }
        }
    }
}