/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Nov 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.jcvi.util.DefaultMemoryMappedFileRange;
import org.jcvi.util.MemoryMappedFileRange;

public class MemoryMappedPhdFileDataStore extends AbstractPhdFileDataStore{

    private final MemoryMappedFileRange recordLocations;
    private long currentStartOffset=0;
    private long currentOffset=currentStartOffset;
    private boolean initialized=false;
    private final File phdBall;
    private int currentLineLength;
    public MemoryMappedPhdFileDataStore(File phdBall) throws FileNotFoundException{
        this(phdBall, new DefaultMemoryMappedFileRange());
    }
    /**
     * @param recordLocations
     * @throws FileNotFoundException 
     */
    public MemoryMappedPhdFileDataStore(File phdBall,MemoryMappedFileRange recordLocations) throws FileNotFoundException {
        this.recordLocations = recordLocations;
        this.phdBall = phdBall;
        PhdParser.parsePhd(phdBall, this);
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
        super.visitEndOfFile();
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
        FileInputStream fileInputStream=null;
        try{
            if(!recordLocations.contains(id)){
                throw new DataStoreException(id +" does not exist");
            }
            Range range = recordLocations.getRangeFor(id);
            
            fileInputStream = new FileInputStream(phdBall);
            MappedByteBuffer buf =fileInputStream.getChannel().map(
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
            IOUtil.closeAndIgnoreErrors(fileInputStream);
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
