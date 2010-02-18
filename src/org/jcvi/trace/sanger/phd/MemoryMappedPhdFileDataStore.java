/*
 * Created on Nov 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jcvi.Range;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.IOUtil;
import org.jcvi.util.ByteBufferInputStream;
import org.jcvi.util.MemoryMappedFileRange;

public class MemoryMappedPhdFileDataStore extends AbstractPhdFileDataStore{

    private final MemoryMappedFileRange recordLocations;
    private long currentStartOffset=0;
    private long currentOffset=currentStartOffset;
    private boolean initialized=false;
    private final File phdBall;
    private int currentLineLength;
    
    /**
     * @param recordLocations
     */
    public MemoryMappedPhdFileDataStore(File phdBall,MemoryMappedFileRange recordLocations) {
        this.recordLocations = recordLocations;
        this.phdBall = phdBall;
    }

    @Override
    public synchronized void visitLine(String line) {
        super.visitLine(line);
        currentLineLength = line.length();
        currentOffset +=currentLineLength;
    }

    private void checkIfNotYetInitialized(){
        if(!initialized){
            throw new IllegalStateException("not yet initialized");
        }
    }
    
    
    @Override
    protected synchronized void visitPhd(String id, List<NucleotideGlyph> bases,
            List<PhredQuality> qualities, List<ShortGlyph> positions,
            Properties comments, List<PhdTag> tags) {
        long endOfOldRecord = currentOffset-currentLineLength-1;
        recordLocations.put(id, Range.buildRange(currentStartOffset,endOfOldRecord));
        currentStartOffset=endOfOldRecord;
    }

    @Override
    public synchronized void visitEndOfFile() {
        initialized=true;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        checkIfNotYetInitialized();
        return recordLocations.contains(id);
    }

    @Override
    public Phd get(String id) throws DataStoreException {
        checkIfNotYetInitialized();
        FileChannel fastaFileChannel=null;
        DefaultPhdFileDataStore dataStore=null;
        InputStream in=null;
        try{
            Range range = recordLocations.getRangeFor(id);
            MappedByteBuffer buf =new FileInputStream(phdBall).getChannel().map(
                    FileChannel.MapMode.READ_ONLY, range.getStart(), range.size());
            
            in =new ByteBufferInputStream(buf);
            dataStore = new DefaultPhdFileDataStore();
            PhdParser.parsePhd(in, dataStore);
            return dataStore.get(id);
            
        } catch (IOException e) {
           throw new DataStoreException("error getting "+ id, e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(fastaFileChannel);
            IOUtil.closeAndIgnoreErrors(dataStore);
            IOUtil.closeAndIgnoreErrors(in);
        }
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        checkIfNotYetInitialized();
        return recordLocations.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        checkIfNotYetInitialized();
        return recordLocations.size();
    }

    @Override
    public void close() throws IOException {
        
        recordLocations.close();
        
    }

    

}
