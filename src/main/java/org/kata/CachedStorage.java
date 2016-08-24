package org.kata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CachedStorage<T> implements Storage<T> {
    private static final Logger LOG = Logger.getLogger(
            CachedStorage.class.getName());

    private final static int DEFAULT_CACHE_SIZE = 8192;
    private Map<Long, T> cache = new HashMap<>();
    private Storage<T> storage;
    private final int cacheSize;

    public CachedStorage(Storage<T> storage) {
        this(storage, DEFAULT_CACHE_SIZE);
    }

    public CachedStorage(Storage<T> storage, int cacheSize) {
        this.storage = storage;
        this.cacheSize = cacheSize;
    }

    @Override
    public long create(T object) throws IOException {
        Long handle = storage.create(object);
        if (cache.size() >= cacheSize) {
            flush();
            cache.clear();
        }
        cache.put(handle, object);
        return handle;
    }

    @Override
    public void update(T object, long handle) throws IOException {
        cache.put(handle, object);
    }

    @Override
    public T load(long handle) throws IOException, ClassNotFoundException {
        T object = cache.get(handle);
        if (object == null) {
            object = storage.load(handle);
        }
        return object;
    }

    protected boolean isCached(Long handle) {
        return cache.containsKey(handle);
    }

    protected void flush() {
        cache.entrySet().stream().forEach(entry -> {
            Long handle = entry.getKey();
            T object = entry.getValue();
            try {
                storage.update(object, handle);
            } catch (IOException e) {
                LOG.severe("Unable to store element in the storage");
            }
        });
    }
}
