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

package org.jcvi.fasta.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.Range;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.io.IOUtil;
import org.jcvi.util.CloseableIterator;
import org.jcvi.util.DefaultIndexedFileRange;
import org.jcvi.util.IndexedFileRange;

/**
 * @author dkatzel
 *
 *
 */
public class IndexedFastaQFileDataStore implements FastQDataStore<FastQRecord>, FastQFileVisitor{

    private final IndexedFileRange indexFileRange;
    private final FastQQualityCodec qualityCodec;
    private final File file;
    private int currentStartOffset;
    private int currentRecordLength;
    private String currentId;
    
    
    /**
     * @param file
     * @throws FileNotFoundException 
     */
    public IndexedFastaQFileDataStore(File file,FastQQualityCodec qualityCodec,IndexedFileRange indexFileRange) throws FileNotFoundException {
        this.file = file;
        this.qualityCodec = qualityCodec;
        this.indexFileRange = indexFileRange;
        FastQFileParser.parse(file, this);
    }
    public IndexedFastaQFileDataStore(File file,FastQQualityCodec qualityCodec) throws FileNotFoundException {
       this(file, qualityCodec,new DefaultIndexedFileRange());
    }
    @Override
    public void visitLine(String line) {
        final int length = line.length();
        currentRecordLength+=length;
        
    }
    @Override
    public void visitFile() {
        currentStartOffset=0;
        currentRecordLength=0;
        
    }
    @Override
    public void visitEndOfFile() {
    }
    @Override
    public boolean visitBeginBlock(String id, String optionalComment) {
        currentId = id;
        return true;
    }
    @Override
    public void visitEndBlock() {
        final Range range = Range.buildRangeOfLength(currentStartOffset, currentRecordLength);
        indexFileRange.put(currentId, range);
        currentStartOffset+=currentRecordLength;
        currentRecordLength=0;
    }
    @Override
    public void visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
        //no-op
    }
    @Override
    public void visitEncodedQualities(String encodedQualities) {
        //no-op
        
    }
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return indexFileRange.getIds();
    }
    @Override
    public FastQRecord get(String id) throws DataStoreException {
        if(!contains(id)){
            throw new DataStoreException(id +" does not exist in datastore");
        }
        Range range =indexFileRange.getRangeFor(id);
        InputStream in =null;
        try {
            in = IOUtil.createInputStreamFromFile(file,range);
            DefaultFastQFileDataStore datastore = new DefaultFastQFileDataStore(qualityCodec);
            FastQFileParser.parse(in, datastore);
            return datastore.get(id);
        } catch (IOException e) {
            throw new DataStoreException("error reading fastq file",e);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        try{
        return indexFileRange.contains(id);
        }catch(IllegalStateException e){
            throw new DataStoreException("error quering index", e);
        }
    }
    @Override
    public int size() throws DataStoreException {
        return indexFileRange.size();
    }
    @Override
    public void close() throws IOException {
        indexFileRange.close();
        
    }
    @Override
    public CloseableIterator<FastQRecord> iterator() {
        try {
            return new LargeFastQFileIterator(file, qualityCodec);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
    
    
    
}
