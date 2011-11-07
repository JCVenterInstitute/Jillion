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
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.frg;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.IndexedFileRange;
import org.jcvi.common.core.util.iter.CloseableIterator;
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
public class IndexedFragmentDataStore extends AbstractFragmentDataStore{

    private final IndexedFileRange fragmentInfoIndexFileRange, mateInfoIndexFileRange;
    private final FileChannel fragFile;
    private final Frg2Parser parser;
    private int currentStart=0;
    private int currentPosition=-1;
    
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
    
    public IndexedFragmentDataStore(File file, IndexedFileRange fragmentInfoIndexFileRange, IndexedFileRange mateInfoIndexFileRange, Frg2Parser parser) throws FileNotFoundException{
        this.fragmentInfoIndexFileRange = fragmentInfoIndexFileRange;
        this.mateInfoIndexFileRange = mateInfoIndexFileRange;
        this.parser = parser;
        this.fragFile = new RandomAccessFile(file, "r").getChannel();
    }
    public IndexedFragmentDataStore(File file, Frg2Parser parser) throws FileNotFoundException{
        this(file, new DefaultIndexedFileRange(), new DefaultIndexedFileRange(),parser);
    }
    @Override
    public void visitFragment(FrgAction action, String fragmentId,
            String libraryId, NucleotideSequence bases,
            QualitySequence qualities, Range validRange,
            Range vectorClearRange, String source) {
        throwErrorIfAlreadyInitialized();
        if(this.isAddOrModify(action)){
            Range fragmentRange = Range.buildRange(currentStart, currentPosition);
            fragmentInfoIndexFileRange.put(fragmentId, fragmentRange);
            updateRangeStartPosition();
        }
        else if (this.isDelete(action)){
            fragmentInfoIndexFileRange.remove(fragmentId);
        }
        
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
        return fragmentInfoIndexFileRange.contains(fragmentId);
    }

    @Override
    public Fragment get(String id) throws DataStoreException {
        throwErrorIfClosed();
        Range range =fragmentInfoIndexFileRange.getRangeFor(id);
        InputStream in = getInputStreamFor(range);
        final SingleFragVisitor singleFragVisitor = new SingleFragVisitor();
        parser.parse(in, singleFragVisitor);
        return singleFragVisitor.getFragmentToReturn();
    }
    private InputStream getInputStreamFor(Range range)
            throws DataStoreException {
        ByteBuffer buffer;
        try {
            buffer = fragFile.map(FileChannel.MapMode.READ_ONLY, range.getStart(), range.size());
        } catch (IOException e) {
            throw new DataStoreException("error memory mapping file ", e);
        }
        byte[] array = new byte[(int)range.size()];
        buffer.get(array);
        InputStream in = new ByteArrayInputStream(array);
        return in;
    }

    @Override
    public CloseableIterator<String> getIds() {
        throwErrorIfClosed();
        return fragmentInfoIndexFileRange.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        throwErrorIfClosed();
        return fragmentInfoIndexFileRange.size();
    }

    @Override
    public void close() throws IOException {
        super.close();
        fragmentInfoIndexFileRange.close();
        fragFile.close();
    }

    @Override
    public Fragment getMateOf(Fragment fragment) throws DataStoreException {
        throwErrorIfClosed();
        final String fragId = fragment.getId();
        String mateId = getMateIdOf(fragId);
        return get(mateId);
    }
    @Override
    public boolean hasMate(Fragment fragment) throws DataStoreException {
        throwErrorIfClosed();
        final String fragId = fragment.getId();
        return getMateIdOf(fragId) !=null;
    }
    private String getMateIdOf(final String fragId) throws DataStoreException {
        Range range = mateInfoIndexFileRange.getRangeFor(fragId);
        InputStream in = getInputStreamFor(range);
        SingleLinkVisitor singleLinkVisitor = new SingleLinkVisitor(fragId);
        parser.parse(in, singleLinkVisitor);
        return singleLinkVisitor.getMateId();
    }
    @Override
    public void visitLink(FrgAction action, List<String> fragIds) {
        throwErrorIfAlreadyInitialized();
        if(this.isAddOrModify(action)){
            Range fragmentRange = Range.buildRange(currentStart, currentPosition);
            for(String fragmentId: fragIds){
                mateInfoIndexFileRange.put(fragmentId, fragmentRange);
            }
            updateRangeStartPosition();
        }
        else if (this.isDelete(action)){
            for(String fragmentId: fragIds){
                mateInfoIndexFileRange.remove(fragmentId);
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
                MateOrientation orientation, Distance distance) {}
        @Override
        public void visitLink(FrgAction action, List<String> fragIds) {}
        @Override
        public void visitEndOfFile(){}
        @Override
        public void visitLine(String line) {}

        @Override
        public void visitFile() {            
        }
    }
    
    
    private static class SingleLinkVisitor implements Frg2Visitor{
        private String mateId=null;
        
        
        private String fragmentIdToGetMateOf;
        
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
                Range vectorClearRange, String source) {}

        @Override
        public void visitLibrary(FrgAction action, String id,
                MateOrientation orientation, Distance distance) {}
        @Override
        public void visitLink(FrgAction action, List<String> fragIds) {
            for(String fragId : fragIds){
                if(!fragId.equals(fragmentIdToGetMateOf)){
                    mateId = fragId;
                }
            }
        }
        @Override
        public void visitEndOfFile(){}
        @Override
        public void visitLine(String line) {}
        @Override
        public void visitFile() {            
        }
    }
    
}
