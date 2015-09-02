package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.MergedSortedRecordIterator;

class TmpDirSortedFastqWriter implements FastqWriter{

    private final File tmpDir;
    private final List<File> tmpFiles = new ArrayList<File>();
    private final int cacheSize;
    private final Comparator<FastqRecord> comparator;
    private final Set<FastqRecord> cache;
    private final FastqQualityCodec codec;
    private final FastqWriter finalWriter;
    
    private volatile boolean isClosed;
 
    public TmpDirSortedFastqWriter(FastqWriter finalWriter, Comparator<FastqRecord> comparator, FastqQualityCodec codec,
    		File tmpDir, int cacheSize) {
        this.tmpDir = tmpDir;
        this.cacheSize = cacheSize;
        this.comparator = comparator;
        this.cache = new TreeSet<>(comparator);
        this.codec = codec;
        this.finalWriter = finalWriter;
    }

    @Override
    public void close() throws IOException {
        if(isClosed){
            return;
        }
        //we might have records in our inmemory cache
        //as well as temp files.
        //they are each sorted so we can merge them
        isClosed=true;
        List<StreamingIterator<FastqRecord>> iters = new ArrayList<>();
        try{
            iters.add(IteratorUtil.createPeekableStreamingIterator(cache.iterator()));
            for(File tmpFile : tmpFiles){
                
                try {
                    iters.add(LargeFastqFileDataStore.create(tmpFile, codec).iterator());
                } catch (DataStoreException e) {
                   throw new IOException("error re-parsing temp file "  + tmpFile.getAbsolutePath(), e);
                }
                 
            }
            
            Iterator<FastqRecord> mergedIter = new MergedSortedRecordIterator<>(iters, comparator);
            try{
                while(mergedIter.hasNext()){
                    finalWriter.write(mergedIter.next());
                }
            }finally{
                finalWriter.close();
            }
        }finally{
           for(StreamingIterator<FastqRecord> iter : iters){
               IOUtil.closeAndIgnoreErrors(iter);
           }
           cache.clear();
           for(File tmpDir : tmpFiles){
               //ignore deletion error
               tmpDir.delete();
           }
           tmpFiles.clear();
        }
        
    }
    private void writeToCache(FastqRecord record) throws IOException{
        cache.add(record);
        if(cache.size() >=cacheSize ){
            writeCacheToTmpFile();
            cache.clear();
        }
        
    }

    private void writeCacheToTmpFile() throws IOException {
        File tmpFile = File.createTempFile("sorted.", ".fastq", tmpDir);
        try(FastqWriter writer = new FastqWriterBuilder(tmpFile)
                                        .qualityCodec(codec)
                                        .build()){
            for(FastqRecord record : cache){
                writer.write(record);
            }
        }
        tmpFiles.add(tmpFile);
        
    }
    @Override
    public void write(FastqRecord record) throws IOException {
        if(isClosed){
            throw new IOException("writer is closed");
        }
        Objects.requireNonNull(record);
        writeToCache(record);
        
    }

    @Override
    public void write(String id, NucleotideSequence nucleotides,
            QualitySequence qualities) throws IOException {
        write(new FastqRecordBuilder(id, nucleotides, qualities).build());
        
    }

    @Override
    public void write(String id, NucleotideSequence sequence,
            QualitySequence qualities, String optionalComment)
            throws IOException {
        write(new FastqRecordBuilder(id, sequence, qualities)
                    .comment(optionalComment)
                    .build());
    }

}
