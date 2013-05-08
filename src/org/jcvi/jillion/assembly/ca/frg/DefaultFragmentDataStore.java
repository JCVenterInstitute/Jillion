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
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public final class DefaultFragmentDataStore extends AbstractFragmentDataStore{
    
    private final Map<String, Fragment> fragments = new LinkedHashMap<String, Fragment>();
    private final Map<String,String> fragmentMates = new HashMap<String, String>();
    
    public static FragmentDataStore create(File frgFile) throws FileNotFoundException{
        Frg2Parser parserInstance = new Frg2Parser();
        DefaultFragmentDataStore datastore = new DefaultFragmentDataStore();
        InputStream in = null;
        try{
            in = new FileInputStream(frgFile);
            parserInstance.parse(in, datastore);
            return datastore;
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
       
    }
    
    private DefaultFragmentDataStore(){
    	//can not instantiate
    }
    @Override
    public void visitLink(FrgAction action, List<String> fragIds) {
        throwErrorIfAlreadyInitialized();
        if(fragIds.size() !=2){
            throw new IllegalArgumentException("only supports 1 : 1 mate pairs");
        }
        String forward = fragIds.get(0);
        String reverse = fragIds.get(1);
        if(isAddOrModify(action)){
            fragmentMates.put(forward, reverse);
            fragmentMates.put(reverse, forward);
        }
        else if(isDelete(action)){
            fragmentMates.remove(forward);
            fragmentMates.remove(reverse);
        }
    }
    @Override
    public Fragment getMateOf(Fragment fragment) throws DataStoreException {
        throwErrorIfClosed();
        if(!hasMate(fragment)){
            return null;
        }
        final String mateId = fragmentMates.get(fragment.getId());        
        return get(mateId);
    }

    @Override
    public boolean hasMate(Fragment fragment) {
        throwErrorIfClosed();
        return fragmentMates.containsKey(fragment.getId());
    }
    @Override
    public void visitFragment(FrgAction action, String fragmentId,
            String libraryId, NucleotideSequence bases,
            QualitySequence qualities, Range validRange,
            Range vectorClearRange, String source) {
        throwErrorIfAlreadyInitialized();
        if(isAddOrModify(action)){
            Library library;
            try {
                library = getLibrary(libraryId);
            } catch (DataStoreException e) {
                throw new IllegalStateException("Fragment uses library "+ libraryId + "before it is declared",e);
            }
            Fragment frag = new DefaultFragment(fragmentId, bases, qualities, 
                    validRange, vectorClearRange, library, source);
            fragments.put(fragmentId, frag);
        }
        else if(isDelete(action)){
            fragments.remove(fragmentId);
        }
        
    }

    @Override
    public void visitLine(String line) {
        //no-op
        
    }
    @Override
    public boolean contains(String fragmentId) throws DataStoreException {
        throwErrorIfClosed();
        return fragments.containsKey(fragmentId);
    }

    @Override
    public Fragment get(String id) throws DataStoreException {
        throwErrorIfClosed();
        return fragments.get(id);
    }

    @Override
    public StreamingIterator<String> idIterator() {
        throwErrorIfClosed();
        return IteratorUtil.createStreamingIterator(fragments.keySet().iterator());
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException {
        throwErrorIfClosed();
        return fragments.size();
    }
}
