package org.kata;

import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class CachedStorageTest {
    @Test
    public void whenCacheIsNotFullTheStoredObjectGoesToCache() throws Exception {
        Storage<String> storage = new FileBasedStorage<>();
        CachedStorage<String> cachedStorage = new CachedStorage<>(storage);
        String data = "data";

        Long handle = cachedStorage.create(data);

        assertTrue(cachedStorage.isCached(handle));
    }

    @Test
    public void whenCacheIsFullThenItsContentGoesToOriginalStorage() throws Exception {
        Storage<String> storage = Mockito.spy(new FileBasedStorage<>());
        CachedStorage<String> cachedStorage = new CachedStorage<>(storage, 1);
        String first = "first";
        String second = "second";

        Long firstHandle = cachedStorage.create(first);
        Long secondHandle = cachedStorage.create(second);

        verify(storage, atLeastOnce()).update(first, firstHandle);

        assertFalse(cachedStorage.isCached(firstHandle));
        assertTrue(cachedStorage.isCached(secondHandle));
    }

    @Test
    public void onUpdateRequestUpdateObjectInCache() throws Exception {
        Storage<String> storage = Mockito.spy(new FileBasedStorage<>());
        CachedStorage<String> cachedStorage = new CachedStorage<>(storage);

        String data = "data";
        Long handle = cachedStorage.create(data);
        String newData = "new-data";
        cachedStorage.update(newData, handle);

        String restoredData = cachedStorage.load(handle);

        verify(storage, never()).update(newData, handle);
        assertThat(restoredData, is(newData));
        assertTrue(cachedStorage.isCached(handle));
    }

    @Test
    public void whenObjectIsInCacheLoadItFromCache() throws Exception {
        Storage<String> storage = Mockito.spy(new FileBasedStorage<>());
        CachedStorage<String> cachedStorage = new CachedStorage<>(storage);

        String data = "data";
        Long handle = cachedStorage.create(data);

        String restoredData = cachedStorage.load(handle);

        verify(storage, never()).load(handle);
        assertThat(restoredData, is(data));
        assertTrue(cachedStorage.isCached(handle));
    }

    @Test
    public void whenObjectIsNotInCacheLoadItFromOriginalStorage() throws Exception {
        Storage<String> storage = Mockito.spy(new FileBasedStorage<>());
        CachedStorage<String> cachedStorage =
                new CachedStorage<>(storage, 1);

        String data = "data";
        Long handle = cachedStorage.create(data);
        cachedStorage.create("other data");

        String restoredData = cachedStorage.load(handle);

        verify(storage, atLeastOnce()).load(handle);
        assertThat(restoredData, is(data));
    }
}