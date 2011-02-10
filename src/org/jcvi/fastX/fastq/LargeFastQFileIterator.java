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

package org.jcvi.fastX.fastq;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.util.CloseableIterator;

/**
 * {@code LargeFastQFileIterator} is an Iterator of FastQRecords meant for large
 * fastq files (although small fastqs will work too).
 * @author dkatzel
 *
 *
 */
public class LargeFastQFileIterator extends AbstractFastQFileVisitor implements CloseableIterator<FastQRecord>{

    private Object endOfFileToken = new Object();
    private BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>(1);
    private Object nextRecord=null;
    private boolean isClosed=false;
    
    public LargeFastQFileIterator(final File fastQFile,FastQQualityCodec qualityCodec) throws InterruptedException{
        super(qualityCodec);
        new Thread(){

            @Override
            public void run() {
                try {
                    FastQFileParser.parse(fastQFile, LargeFastQFileIterator.this);
                } catch (IOException e) {
                    //should never happen
                    throw new RuntimeException(e);
                }
            }
            
        }.start();
        blockingGetNextRecord();
    }
    /**
     * @throws InterruptedException 
     * 
     */
    private void blockingGetNextRecord() throws InterruptedException {
        nextRecord = queue.take();            
    }
    @Override
    public boolean visitBeginBlock(String id, String optionalComment) {
        super.visitBeginBlock(id, optionalComment);
        return !isClosed;
    }
    @Override
    public void visitEndOfFile() {
        blockingPut(endOfFileToken);
    }
    private void blockingPut(Object obj){
        try {
            queue.put(obj);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
    /**
     * {@inheritDoc}
     */
     @Override
     protected boolean visitFastQRecord(String id,
             NucleotideEncodedGlyphs nucleotides,
             QualityEncodedGlyphs qualities, String optionalComment) {
         FastQRecord record = new DefaultFastQRecord(id,nucleotides, qualities,optionalComment);
         blockingPut(record);
         return true;
     }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean hasNext() {
        return nextRecord !=endOfFileToken;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        isClosed=true;
        nextRecord=endOfFileToken;
        //remove element from queue
        queue.poll();            
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public FastQRecord next() {
        if(!hasNext()){
            throw new NoSuchElementException("no more fasta records");
        }
        FastQRecord next = (FastQRecord)nextRecord;
        try {
            blockingGetNextRecord();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return next;
    }
    
    
    
}
