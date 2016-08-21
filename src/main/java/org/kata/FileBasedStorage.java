package org.kata;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * <code>FileBasedStorage</code> is simple implementation of storage backed with
 * temporal file. Stored data is guaranteed to be available while JVM run.
 * Once JVM is stopped the temporal file might be removed (depending on OS
 * type and settings) and all data lost.
 * @param <T> a type of objects stored in this storage
 */
public class FileBasedStorage<T> implements Storage<T> {
    // @to-do: this maps needs to be stored in file non in mem as well
    private Map<Long, Long> offsetInFileByHandle = new HashMap<>();

    private Path dataFile;

    private static long currentHandle = 0;

    public FileBasedStorage() throws IOException {
        this.dataFile = Files.createTempFile(null, null);
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

    private long nextHandle() {
        return currentHandle++;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public T load(long handle) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(dataFile.toFile())) {
            long offset = offsetInFileByHandle.get(handle);
            fis.getChannel().position(offset);
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));
            return (T) ois.readObject();
        }
    }

    @Override
    public void update(T object, long handle) throws IOException {
        offsetInFileByHandle.put(handle, appendObjectToDataFile(object));
    }

    private long appendObjectToDataFile(T object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos);
             FileOutputStream fos = new FileOutputStream(dataFile.toFile(), true))
        {
            oos.writeObject(object);
            long offset = fos.getChannel().position();
            bos.writeTo(fos);
            return offset;
        }
    }
}