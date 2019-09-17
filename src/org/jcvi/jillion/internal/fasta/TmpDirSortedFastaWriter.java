/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.fasta;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaDataStore;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.fasta.FastaWriter;
import org.jcvi.jillion.internal.core.util.iter.MergedSortedRecordIterator;

public abstract class TmpDirSortedFastaWriter<S, T extends Sequence<S>, F extends FastaRecord<S,T>> implements FastaWriter<S,T,F>{

    private final File tmpDir;
    private final List<File> tmpFiles = new ArrayList<File>();
    private final int cacheSize;
    private final Comparator<F> comparator;
    private final Set<F> cache;
   
    private final FastaWriter<S,T,F> finalWriter;
    
    private volatile boolean isClosed;
 
    public TmpDirSortedFastaWriter(FastaWriter<S,T,F> finalWriter, Comparator<F> comparator,
    		File tmpDir, int cacheSize) {
        this.tmpDir = tmpDir;
        this.cacheSize = cacheSize;
        this.comparator = comparator;
        this.cache = new TreeSet<>(comparator);
        this.finalWriter = finalWriter;
    }

    @Override
    public synchronized void close() throws IOException {
        if(isClosed){
            return;
        }
        isClosed=true;
        //we might have records in our inmemory cache
        //as well as temp files.
        //they are each sorted so we can merge them
       
        List<StreamingIterator<F>> iters = new ArrayList<>();
        try{
            iters.add(IteratorUtil.createPeekableStreamingIterator(cache.iterator()));
            for(File tmpFile : tmpFiles){
                
                try {
                    iters.add(createStreamingIteratorFor(tmpFile));
                } catch (DataStoreException e) {
                   throw new IOException("error re-parsing temp file "  + tmpFile.getAbsolutePath(), e);
                }
                 
            }
            
            Iterator<F> mergedIter = new MergedSortedRecordIterator<>(iters, comparator);
            try{
                while(mergedIter.hasNext()){
                    finalWriter.write(mergedIter.next());
                }
            }finally{
                finalWriter.close();
            }
        }finally{
           for(StreamingIterator<F> iter : iters){
               IOUtil.closeAndIgnoreErrors(iter);
           }
           cache.clear();
           for(File tmpDir : tmpFiles){
               //ignore deletion error
        	   IOUtil.deleteIgnoreError(tmpDir);
             
           }
           tmpFiles.clear();
        }
    }
    private synchronized void writeToCache(F record) throws IOException{
        cache.add(record);
        if(cache.size() >=cacheSize ){
            writeCacheToTmpFile();
            cache.clear();
        }
        
    }
    protected abstract StreamingIterator<F> createStreamingIteratorFor(File tmpFastaFile) throws IOException, DataStoreException;
    
    protected abstract FastaWriter<S,T,F> createNewTmpWriter(File tmpFile) throws IOException;

    protected abstract F createFastaRecord(String id, T sequence, String optionalComment);
    
    private void writeCacheToTmpFile() throws IOException {
        File tmpFile = File.createTempFile("sorted.", ".fastq", tmpDir);
        try(FastaWriter<S,T,F> writer = createNewTmpWriter(tmpFile)){
            for(F record : cache){
                writer.write(record);
            }
        }
        tmpFiles.add(tmpFile);
        
    }
    @Override
    public void write(F record) throws IOException {
        if(isClosed){
            throw new IOException("writer is closed");
        }
        Objects.requireNonNull(record);
        writeToCache(record);
        
    }

    @Override
    public void write(String id, T sequence) throws IOException {
        write(id, sequence, null);
        
    }

    @Override
    public void write(String id, T sequence, String optionalComment)
            throws IOException {
        write(createFastaRecord(id, sequence, optionalComment));
    }

}
