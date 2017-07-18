package org.jcvi.jillion.internal.core.datastore;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
import org.jcvi.jillion.internal.core.util.Sneak;

public class AbstractMapBackedDataStore<T> extends AbstractDataStore<T>{

    private final Map<String, T> map;
    
    public AbstractMapBackedDataStore(Map<String, T> map){
        this.map = Objects.requireNonNull(map);
    }
    @Override
    protected void handleClose() throws IOException {
       map.clear();
    }

    @Override
    protected boolean containsImpl(String id) throws DataStoreException {
        return map.containsKey(id);
    }

    @Override
    protected T getImpl(String id) throws DataStoreException {
        return map.get(id);
    }

    @Override
    protected long getNumberOfRecordsImpl() throws DataStoreException {
        return map.size();
    }

    @Override
    protected StreamingIterator<String> idIteratorImpl()
            throws DataStoreException {
        return DataStoreStreamingIterator.create(this, map.keySet().iterator());
    }

    @Override
    protected StreamingIterator<T> iteratorImpl() throws DataStoreException {
        return DataStoreStreamingIterator.create(this, map.values().iterator());
    }

    @Override
    protected StreamingIterator<DataStoreEntry<T>> entryIteratorImpl()
            throws DataStoreException {
        return DataStoreStreamingIterator.create(this, 
                IteratorUtil.map(map.entrySet().iterator(), e -> new DataStoreEntry<>(e.getKey(), e.getValue())));
    }
    @Override
    public <E extends Throwable> void forEach(
            ThrowingBiConsumer<String, T, E> consumer) throws IOException, E {
        map.forEach((id, t) -> {
            try {
                consumer.accept(id, t);
            } catch (Throwable e) {
               Sneak.sneakyThrow(e);
            }
        });
    }

}
