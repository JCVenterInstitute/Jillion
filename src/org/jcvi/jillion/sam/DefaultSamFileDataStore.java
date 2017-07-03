package org.jcvi.jillion.sam;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.util.streams.ThrowingConsumer;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.core.util.Sneak;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
import org.jcvi.jillion.sam.header.SamHeader;

class DefaultSamFileDataStore implements SamFileDataStore {

    protected final SamParser parser;
    private final Predicate<SamRecord> filter;
    
    private volatile boolean isClosed;
    
    private long numRecords = -1;
    
    DefaultSamFileDataStore(SamParser parser, Predicate<SamRecord> filter) {
        this.parser = parser;
        this.filter = filter;
    }

    private void verifyNotClosed() throws DataStoreException{
        if(isClosed){
            throw new DataStoreException("closed");
        }
    }
    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        verifyNotClosed();
        return new StreamingIterator<String>(){
                StreamingIterator<SamRecord> iter = iterator();
                @Override
                public boolean hasNext() {
                        return iter.hasNext();
                }

                @Override
                public void close() {
                        iter.close();
                }

                @Override
                public String next() {
                    SamRecord next = iter.next();
                    return next.getQueryName();
                }

                @Override
                public void remove() {
                        iter.remove();
                }
                
        };
    }

    @Override
    public SamRecord get(String id) throws DataStoreException {
        verifyNotClosed();
        Objects.requireNonNull(id);
        SamRecord found;
        try {
            found = getRecord(id);
            if(found == null || (filter !=null && !filter.test(found))){
                return null;
            }
            return found;
        } catch (IOException e) {
           throw new DataStoreException("error parsing sam/bam file to find record with id '" + id +"'", e);
        }
       
    }
    
    @Override
    public List<SamRecord> getAllRecordsFor(String id) throws DataStoreException {
        verifyNotClosed();
        Objects.requireNonNull(id);
       
        try {
            List<SamRecord> found = getAllRecord(id);
            List<SamRecord> filtered = new ArrayList<>(found.size());
            for(SamRecord r : found){
                if(found == null || (filter !=null && !filter.test(r))){
                    continue;
                }
                filtered.add(r);
            }
            return filtered;
        } catch (IOException e) {
           throw new DataStoreException("error parsing sam/bam file to find record with id '" + id +"'", e);
        }
       
    }

    protected SamRecord getRecord(String id) throws IOException{
        SamRecord[] ret = new SamRecord[1];
        parser.parse(new AbstractSamVisitor() {

            @Override
            public void visitRecord(SamVisitorCallback callback,
                    SamRecord record, VirtualFileOffset start,
                    VirtualFileOffset end) {
                if(id.equals(record.getQueryName())){
                    ret[0] = record;
                    callback.haltParsing();
                }
            }
            
        });
        return ret[0];
    }
    
    protected List<SamRecord> getAllRecord(String id) throws IOException{
        List<SamRecord> ret = new ArrayList<SamRecord>();
        parser.parse(new AbstractSamVisitor() {

            @Override
            public void visitRecord(SamVisitorCallback callback,
                    SamRecord record, VirtualFileOffset start,
                    VirtualFileOffset end) {
                if(id.equals(record.getQueryName())){
                   ret.add(record);
                }
            }
            
        });
        return ret;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return get(id) !=null;
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException {
        verifyNotClosed();
        synchronized(this){
            if(numRecords == -1){
                try {
                    parser.parse(new AbstractSamVisitor() {
                        long count=0;
                        @Override
                        public void visitRecord(SamVisitorCallback callback,
                                SamRecord record, VirtualFileOffset start,
                                VirtualFileOffset end) {
                             count++;
                        }
                        @Override
                        public void visitEnd() {
                            numRecords = count;
                        }
                        
                    });
                } catch (IOException e) {
                    throw new DataStoreException("error counting number of records", e);
                }
            }
            return numRecords;
        }
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public StreamingIterator<SamRecord> iterator() throws DataStoreException {
        verifyNotClosed();
        FilteredVisitor iter = new FilteredVisitor();
        iter.start();
        
        return DataStoreStreamingIterator.create(this, iter);
    }

    @Override
    public StreamingIterator<DataStoreEntry<SamRecord>> entryIterator()
            throws DataStoreException {
        verifyNotClosed();
        return new StreamingIterator<DataStoreEntry<SamRecord>>(){
                StreamingIterator<SamRecord> iter = iterator();
                @Override
                public boolean hasNext() {
                        return iter.hasNext();
                }

                @Override
                public void close() {
                        iter.close();
                }

                @Override
                public DataStoreEntry<SamRecord> next() {
                    SamRecord next = iter.next();
                        return new DataStoreEntry<>(next.getQueryName(), next);
                }

                @Override
                public void remove() {
                        iter.remove();
                }
                
        };
    }

    @Override
    public void close() {
        isClosed = true;

    }

    @Override
    public SamHeader getHeader() throws DataStoreException {
        verifyNotClosed();
        try {
            return parser.getHeader();
        } catch (IOException e) {
            throw new DataStoreException("error parsing sam/bam file to get header", e);
        }
    }

    @Override
    public <E extends Throwable> void forEachAlignedRecord(String referenceName,
            ThrowingConsumer<SamRecord, E> consumer) throws DataStoreException, E {
        Objects.requireNonNull(referenceName);
        verifyNotClosed();
        try{
        parser.parse(referenceName, new AbstractSamVisitor() {

            @Override
            public void visitRecord(SamVisitorCallback callback,
                    SamRecord record, VirtualFileOffset start,
                    VirtualFileOffset end) {
                if(filter !=null && !filter.test(record)){
                    return;
                }
                try{
                    consumer.accept(record);
                }catch(Throwable t){
                    Sneak.sneakyThrow(t);
                }
            }
            
        });
        }catch (IOException e) {
            throw new DataStoreException("error parsing sam/bam file to get header", e);
        }
        
    }
    
    @Override
    public <E extends Throwable> void forEachAlignedRecord(String referenceName, Range alignmentRange,
            ThrowingConsumer<SamRecord, E> consumer) throws DataStoreException, E {
        Objects.requireNonNull(referenceName);
        Objects.requireNonNull(alignmentRange);
        verifyNotClosed();
        try{
        parser.parse(referenceName,alignmentRange, new AbstractSamVisitor() {

            @Override
            public void visitRecord(SamVisitorCallback callback,
                    SamRecord record, VirtualFileOffset start,
                    VirtualFileOffset end) {
                if(filter !=null && !filter.test(record)){
                    return;
                }
                try{
                    consumer.accept(record);
                }catch(Throwable t){
                    Sneak.sneakyThrow(t);
                }
            }
            
        });
        }catch (IOException e) {
            throw new DataStoreException("error parsing sam/bam file to get header", e);
        }
        
    }

    @Override
    public StreamingIterator<SamRecord> getAlignedRecords(String referenceName)
            throws DataStoreException {
        Objects.requireNonNull(referenceName);
        
        verifyNotClosed();
        verifyValidReference(referenceName);
        SingleReferenceFilteredVisitor iter = new SingleReferenceFilteredVisitor(referenceName);
        iter.start();
        
        return DataStoreStreamingIterator.create(this, iter);
    }

    private void verifyValidReference(String referenceName) throws DataStoreException {
        if(getHeader().getReferenceSequence(referenceName) == null){
            throw new DataStoreException("no reference with name '" + referenceName + "'");
        }
        
    }

    @Override
    public StreamingIterator<SamRecord> getAlignedRecords(String referenceName,
            Range alignmentRange) throws DataStoreException {
        Objects.requireNonNull(referenceName);
        Objects.requireNonNull(alignmentRange);
        verifyNotClosed();
        SingleReferenceAndRangeFilteredVisitor iter = new SingleReferenceAndRangeFilteredVisitor(referenceName, alignmentRange);
        iter.start();
        
        return DataStoreStreamingIterator.create(this, iter);
    }
    
    private class FilteredVisitor extends AbstractBlockingStreamingIterator<SamRecord>{

        @Override
        protected void backgroundThreadRunMethod() throws RuntimeException {
            try {
                parser.parse(new AbstractSamVisitor() {

                    @Override
                    public void visitRecord(SamVisitorCallback callback,
                            SamRecord record, VirtualFileOffset start,
                            VirtualFileOffset end) {
                        if(filter !=null && !filter.test(record)){
                            return;
                        }
                        FilteredVisitor.this.blockingPut(record);
                    }
                    
                });
            } catch (IOException e) {
                throw new UncheckedIOException("error parsing sam/bam file", e);
            }
            
        }
        
    }
    
    
    
    private class SingleReferenceFilteredVisitor extends AbstractBlockingStreamingIterator<SamRecord>{
        private final String refname;
        
        protected SingleReferenceFilteredVisitor(String refname) {
            super();
            this.refname = refname;
        }

        @Override
        protected void backgroundThreadRunMethod() throws RuntimeException {
            try {
                parser.parse(refname, new AbstractSamVisitor() {

                    @Override
                    public void visitRecord(SamVisitorCallback callback,
                            SamRecord record, VirtualFileOffset start,
                            VirtualFileOffset end) {
                        if(filter !=null && !filter.test(record)){
                            return;
                        }
                        SingleReferenceFilteredVisitor.this.blockingPut(record);
                    }
                    
                });
            } catch (IOException e) {
                throw new UncheckedIOException("error parsing sam/bam file", e);
            }
            
        }
        
    }
    
    private class SingleReferenceAndRangeFilteredVisitor extends AbstractBlockingStreamingIterator<SamRecord>{
        private final String refname;
        private final Range range;
        
        protected SingleReferenceAndRangeFilteredVisitor(String refname, Range range) {
            super();
            this.refname = refname;
            this.range = range;
        }

        @Override
        protected void backgroundThreadRunMethod() throws RuntimeException {
            try {
                parser.parse(refname, range, new AbstractSamVisitor() {

                    @Override
                    public void visitRecord(SamVisitorCallback callback,
                            SamRecord record, VirtualFileOffset start,
                            VirtualFileOffset end) {
                        if(filter !=null && !filter.test(record)){
                            return;
                        }
                        SingleReferenceAndRangeFilteredVisitor.this.blockingPut(record);
                    }
                    
                });
            } catch (IOException e) {
                throw new UncheckedIOException("error parsing sam/bam file", e);
            }
            
        }
        
    }

}
