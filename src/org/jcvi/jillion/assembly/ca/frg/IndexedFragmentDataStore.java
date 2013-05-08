/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ca.frg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
/**
 * {@code IndexedFragmentDataStore} is an implementation of 
 * {@link FragmentDataStore} that only stores an index containing
 * file offsets to the various phd records contained
 * inside the phdball file.  This allows large files to provide random 
 * access without taking up much memory.  The downside is each phd
 * must be re-parsed each time.
 * @author dkatzel
 *
 *
 */
public final class IndexedFragmentDataStore extends AbstractFragmentDataStore{

    private final Map<String,Range> fragmentInfoIndexFileRange, mateInfoIndexFileRange;
    private final File fragFile;
    private final Frg2Parser parser;
    private int currentStart=0;
    private int currentPosition=-1;
    private volatile boolean closed;
    
    public static FragmentDataStore create(File frgFile) throws FileNotFoundException{
        Frg2Parser parserInstance = new Frg2Parser();
        IndexedFragmentDataStore datastore = new IndexedFragmentDataStore(frgFile, parserInstance);
        InputStream in = null;
        try{
            in = new FileInputStream(frgFile);
            parserInstance.parse(in, datastore);
            return datastore;
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
       
    }
    
    private IndexedFragmentDataStore(File file, Map<String,Range> fragmentInfoIndexFileRange, Map<String,Range> mateInfoIndexFileRange, Frg2Parser parser){
        this.fragmentInfoIndexFileRange = fragmentInfoIndexFileRange;
        this.mateInfoIndexFileRange = mateInfoIndexFileRange;
        this.parser = parser;
        this.fragFile = file;
    }
    private IndexedFragmentDataStore(File file, Frg2Parser parser){
        this(file, new LinkedHashMap<String,Range>(), new LinkedHashMap<String,Range>(),parser);
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }

    
    protected void throwErrorIfClosed() {
        if(isClosed()){
            throw new DataStoreClosedException("datastore is closed");
        }
        
    }
    @Override
    public void visitFragment(FrgAction action, String fragmentId,
            String libraryId, NucleotideSequence bases,
            QualitySequence qualities, Range validRange,
            Range vectorClearRange, String source) {
        throwErrorIfAlreadyInitialized();
        if(this.isAddOrModify(action)){
            Range fragmentRange = Range.of(currentStart, currentPosition);
            fragmentInfoIndexFileRange.put(fragmentId, fragmentRange);
            updateRangeStartPosition();
        }
        else if (this.isDelete(action)){
        	handleDelete(fragmentId);
        }
        
    }

    private void handleDelete(String frgId){
    	/*
    	 * Delete no longer supported:
    	 * From email from Brian Walenz 2012-08-22
    	 * 
    	 * We can still delete reads from the store, 
    	 * but there are simpler methods than the D action.  
    	 * If the D action is still coded, it is essentially dead.
    	 * 
    	 * The only use case I can think of would be to delete bad
    	 * reads in a partially completed assembly.  
    	 * Advanced usage, and not that common.
    	 */
    	
    	
    }
    @Override
    public void visitLibrary(FrgAction action, String id,
            MateOrientation orientation, Distance distance) {
        super.visitLibrary(action, id, orientation, distance);
        updateRangeStartPosition();
    }
    private void updateRangeStartPosition() {
        currentStart = currentPosition +1;
    }
    
    @Override
    public void visitLine(String line) {
        throwErrorIfAlreadyInitialized();
        currentPosition+=line.length();
        
    }

    @Override
    public boolean contains(String fragmentId) throws DataStoreException {
        throwErrorIfClosed();
        return fragmentInfoIndexFileRange.containsKey(fragmentId);
    }

    @Override
    public Fragment get(String id) throws DataStoreException {
        throwErrorIfClosed();
        Range range =fragmentInfoIndexFileRange.get(id);
        InputStream in;
        try {
            in = IOUtil.createInputStreamFromFile(fragFile, (int)range.getBegin(), (int)range.getLength());
           
            final SingleFragVisitor singleFragVisitor = new SingleFragVisitor();
            parser.parse(in, singleFragVisitor);
            return singleFragVisitor.getFragmentToReturn();
        } catch (IOException e) {
            throw new DataStoreException("error reading frg file", e);
        }
    }
    

    @Override
    public StreamingIterator<String> idIterator() {
        throwErrorIfClosed();
        return DataStoreStreamingIterator.create(this, fragmentInfoIndexFileRange.keySet().iterator());
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException {
        throwErrorIfClosed();
        return fragmentInfoIndexFileRange.size();
    }

    @Override
    public void close() throws IOException {
        super.close();
        closed =true;
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<Fragment> iterator() {
        FrgIterator iter= new FrgIterator();
        iter.start();
        return DataStoreStreamingIterator.create(this, iter);
    }

    @Override
    public Fragment getMateOf(Fragment fragment) throws DataStoreException {
        throwErrorIfClosed();
        final String fragId = fragment.getId();
        try {
            String mateId = getMateIdOf(fragId);
            return get(mateId);
        } catch (IOException e) {
            throw new DataStoreException("error parsing mate info from frg file",e);
        }
        
    }
    @Override
    public boolean hasMate(Fragment fragment) throws DataStoreException {
        throwErrorIfClosed();
        final String fragId = fragment.getId();
        try {
            return getMateIdOf(fragId) !=null;
        } catch (IOException e) {
            throw new DataStoreException("error parsing mate info from frg file",e);
        }
    }
    private String getMateIdOf(final String fragId) throws IOException {
        Range range = mateInfoIndexFileRange.get(fragId);
        InputStream in = IOUtil.createInputStreamFromFile(fragFile, (int)range.getBegin(), (int)range.getLength());
        SingleLinkVisitor singleLinkVisitor = new SingleLinkVisitor(fragId);
        parser.parse(in, singleLinkVisitor);
        return singleLinkVisitor.getMateId();
    }
    @Override
    public void visitLink(FrgAction action, List<String> fragIds) {
        throwErrorIfAlreadyInitialized();
        if(this.isAddOrModify(action)){
            Range fragmentRange = Range.of(currentStart, currentPosition);
            for(String fragmentId: fragIds){
                mateInfoIndexFileRange.put(fragmentId, fragmentRange);
            }
            updateRangeStartPosition();
        }
        else if (this.isDelete(action)){
            for(String fragmentId: fragIds){
            	handleDelete(fragmentId);
            }
        }
        
    }
    
    private class SingleFragVisitor implements Frg2Visitor{
        Fragment fragmentToReturn=null;
        
        public Fragment getFragmentToReturn() {
            return fragmentToReturn;
        }

        @Override
        public void visitFragment(FrgAction action,
                String fragmentId, String libraryId,
                NucleotideSequence bases,
                QualitySequence qualities, Range validRange,
                Range vectorClearRange, String source) {
            Library library;
            try {
                library = getLibrary(libraryId);
            } catch (DataStoreException e) {
                throw new IllegalStateException("Fragment uses library "+ libraryId + "before it is declared",e);
            }
            fragmentToReturn = new DefaultFragment(fragmentId, bases, 
                                    qualities, validRange, vectorClearRange, 
                                    library, source);
            
        }

        @Override
        public void visitLibrary(FrgAction action, String id,
                MateOrientation orientation, Distance distance) {
        	//no-op
        }
        @Override
        public void visitLink(FrgAction action, List<String> fragIds) {
        	//no-op
        }
        @Override
        public void visitEndOfFile(){
        	//no-op
        }
        @Override
        public void visitLine(String line) {
        	//no-op
        }

        @Override
        public void visitFile() {           
        	//no-op
        }
    }
    
    
    private static class SingleLinkVisitor implements Frg2Visitor{
        private String mateId=null;
        
        
        private final String fragmentIdToGetMateOf;
        
        public SingleLinkVisitor(String fragmentIdToGetMateOf){
            this.fragmentIdToGetMateOf = fragmentIdToGetMateOf;
        }
        public String getMateId() {
            return mateId;
        }
        @Override
        public void visitFragment(FrgAction action,
                String fragmentId, String libraryId,
                NucleotideSequence bases,
                QualitySequence qualities, Range validRange,
                Range vectorClearRange, String source) {
        	//no-op
        }

        @Override
        public void visitLibrary(FrgAction action, String id,
                MateOrientation orientation, Distance distance) {
        	//no-op
        }
        @Override
        public void visitLink(FrgAction action, List<String> fragIds) {
            for(String fragId : fragIds){
                if(!fragId.equals(fragmentIdToGetMateOf)){
                    mateId = fragId;
                }
            }
        }
        @Override
        public void visitEndOfFile(){
        	//no-op
        }
        @Override
        public void visitLine(String line) {
        	//no-op
        }
        @Override
        public void visitFile() {        
        	//no-op
        }
    }
    
    
    private class FrgIterator extends AbstractBlockingStreamingIterator<Fragment>{

        /**
        * {@inheritDoc}
        */
        @Override
        protected void backgroundThreadRunMethod() {
            Frg2Visitor visitor = new Frg2Visitor(){

                @Override
                public void visitLine(String line) {
                	//no-op
                }

                @Override
                public void visitFile() {
                	//no-op
                }

                @Override
                public void visitEndOfFile() {
                	//no-op
                }

                @Override
                public void visitLibrary(FrgAction action, String id,
                        MateOrientation orientation, Distance distance) {}

                @Override
                public void visitFragment(FrgAction action, String fragmentId,
                        String libraryId, NucleotideSequence bases,
                        QualitySequence qualities, Range validRange,
                        Range vectorClearRange, String source) {
                    if(fragmentInfoIndexFileRange.containsKey(fragmentId)){
                        //only add frgs we care about
                        try {
                            Library library = getLibrary(libraryId);
                            Fragment frg =  new DefaultFragment(fragmentId, bases, 
                                    qualities, validRange, vectorClearRange, 
                                    library, source);
                            FrgIterator.this.blockingPut(frg);
                        } catch (DataStoreException e) {
                            throw new IllegalStateException("could not get library "+ libraryId,e);
                        }
                    }
                    
                }

                @Override
                public void visitLink(FrgAction action, List<String> fragIds) {
                	//no-op
                }
                
            };
            InputStream in =null;
            try{
                in = new FileInputStream(fragFile);
                new Frg2Parser().parse(in, visitor);
            } catch (FileNotFoundException e) {
               throw new IllegalStateException("error reading frg file",e);
            }finally{
                IOUtil.closeAndIgnoreErrors(in);
            }
        }
        
    }
    
}
