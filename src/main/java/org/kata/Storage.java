package org.kata;

import java.io.IOException;
import java.io.Serializable;

/**
 * Defines the contract for storage service
 * @param <T> a type of objects that a given storage stores/loads
 */
public interface Storage<T> extends Serializable {
    /**
     * Creates object in the storage
     * @param object an object to store
     * @return a handle object that can be used to load object back from
     * storage later
     * @throws IOException if could not write object into the storage
     */
    long create(T object) throws IOException;

    /**
     * Update object in the storage
     * @param object an new object state that needs to persisted in the storage
     * @param handle a handle that is required to find corresponding
     *               object data in the storage that needs to be updated
     * @throws IOException if could not update object in the storage
     */
    void update(T object, long handle) throws IOException;

    /**
     * Loads object from storage
     * @param handle a handle of object to load from storage
     * @return restored from storage object
     * @throws IOException if could not read object from storage
     * @throws ClassNotFoundException if class of object stored in the storage
     * not available/could not be found in runtime
     */
    T load(long handle) throws IOException, ClassNotFoundException;
}