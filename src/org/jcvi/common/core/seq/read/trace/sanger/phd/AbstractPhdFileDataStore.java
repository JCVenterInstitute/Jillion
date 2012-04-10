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
 * Created on Nov 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreIterator;
import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.ShortGlyphFactory;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.CloseableIterator;

public abstract class AbstractPhdFileDataStore implements PhdDataStore, PhdFileVisitor{

    private static final ShortGlyphFactory PEAK_FACTORY = ShortGlyphFactory.getInstance();
    private NucleotideSequenceBuilder currentBases = new NucleotideSequenceBuilder();
    private List<PhredQuality> currentQualities = new ArrayList<PhredQuality>();
    private List<ShortSymbol> currentPositions = new ArrayList<ShortSymbol>();
    private List<PhdTag> tags = new ArrayList<PhdTag>();
    private boolean initialized = false;
    private Properties currentComments;
    private String currentId; 
    private String currentTag;
    private boolean inTag=false;
    private StringBuilder currentTagValueBuilder;
    
    private final DataStoreFilter filter;
    private boolean closed = false;
    
    @Override
    public synchronized void close() throws IOException {
        closed =true;        
    }
    protected synchronized void checkNotYetClosed(){
        if(closed){
            throw new IllegalStateException("datastore already closed");
        }
    }
    
    
    @Override
    public boolean isClosed() throws DataStoreException {
        return closed;
    }



    public AbstractPhdFileDataStore(){
        this(AcceptingDataStoreFilter.INSTANCE);
    }
    
    public AbstractPhdFileDataStore(DataStoreFilter filter){
        this.filter= filter;
    }
    
    protected abstract void visitPhd(String id,
            List<Nucleotide> bases,
            List<PhredQuality> qualities,
            List<ShortSymbol> positions, 
            Properties comments,
            List<PhdTag> tags);
    
    @Override
    public synchronized void visitBasecall(Nucleotide base, PhredQuality quality,
            int tracePosition) {
        checkNotYetInitialized();
        currentBases.append(base);
       currentQualities.add(quality);
       currentPositions.add(PEAK_FACTORY.getGlyphFor(tracePosition));            
    }

    protected void resetCurrentValues(){
        currentBases= new NucleotideSequenceBuilder();
        currentQualities= new ArrayList<PhredQuality>();
        currentPositions= new ArrayList<ShortSymbol>();
        tags = new ArrayList<PhdTag>();
    }

    protected void checkNotYetInitialized() {
        if(initialized){
            throw new IllegalStateException("can only create 1 phd multiple phds detected");
        }
    }
    protected void checkAlreadyInitialized() {
        if(!initialized){
            throw new IllegalStateException("can only create 1 phd multiple phds detected");
        }
    }


    @Override
    public void visitBeginDna() {
        checkNotYetInitialized();
    }



    @Override
    public synchronized void visitBeginSequence(String id) {
        checkNotYetInitialized();
        if(currentId !=null && filter.accept(currentId)){
                visitPhd(currentId, currentBases.asList(), currentQualities, currentPositions, currentComments,tags);
        }
        this.currentId = id;
        resetCurrentValues();
    }



    @Override
    public synchronized void visitComment(Properties comments) {
        checkNotYetInitialized();
        this.currentComments = comments;
    }



    @Override
    public synchronized void visitEndDna() {        
    }



    @Override
    public synchronized void visitEndSequence() {
        checkNotYetInitialized();
    }

    


    /**
    * {@inheritDoc}
    */
    @Override
    public boolean visitBeginPhd(String id) {
        return true;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean visitEndPhd() {
        return true;
    }
    @Override
    public synchronized void visitLine(String line) {
        checkNotYetInitialized();
        if(inTag){
            currentTagValueBuilder.append(line);
        }
    }



    @Override
    public synchronized void visitEndOfFile() {
        checkNotYetInitialized();
        if(currentId !=null){
            visitPhd(currentId, currentBases.asList(), currentQualities, currentPositions, currentComments,tags);
        }
        resetCurrentValues();
        initialized=true;
    }



    @Override
    public synchronized void visitFile() {
        checkNotYetInitialized();
    }


    @Override
    public CloseableIterator<Phd> iterator() {
        return new DataStoreIterator<Phd>(this);
    }

    @Override
    public synchronized void visitBeginTag(String tagName) {
        checkNotYetInitialized();
        currentTag =tagName;
        currentTagValueBuilder = new StringBuilder();
        inTag =true;
    }

    @Override
    public synchronized void visitEndTag() {
        checkNotYetInitialized();
        if(!inTag){
            throw new IllegalStateException("invalid tag");
        }
        tags.add(new DefaultPhdTag(currentTag, currentTagValueBuilder.toString()));
        inTag = false;
    }

}
