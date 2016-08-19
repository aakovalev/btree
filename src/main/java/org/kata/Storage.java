package org.kata;

import java.io.IOException;

/**
 * Defines the contract for storage service
 * @param <T> a type of objects that a given storage stores/loads
 */
public interface Storage<T> {
    /**
     * Saves object in the storage
     * @param object an object to store
     * @return a handle object that can be used to load object back from
     * storage later
     * @throws IOException if could not write object into the storage
     */
    long save(T object) throws IOException;

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