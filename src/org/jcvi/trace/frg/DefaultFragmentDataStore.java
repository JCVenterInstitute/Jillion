/*
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jcvi.Range;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Library;

public class DefaultFragmentDataStore extends AbstractFragmentDataStore{
    
    private final Map<String, Fragment> fragments = new HashMap<String, Fragment>();
    private final Map<String,String> fragmentMates = new HashMap<String, String>();
    
    
    @Override
    public void visitLink(FrgVisitorAction action, List<String> fragIds) {
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
    public void visitFragment(FrgVisitorAction action, String fragmentId,
            String libraryId, NucleotideEncodedGlyphs bases,
            EncodedGlyphs<PhredQuality> qualities, Range validRange,
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
    public Iterator<String> getIds() {
        throwErrorIfClosed();
        return fragments.keySet().iterator();
    }

    @Override
    public int size() throws DataStoreException {
        throwErrorIfClosed();
        return fragments.size();
    }
}
