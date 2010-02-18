/*
 * Created on Dec 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.jcvi.Range;
import org.jcvi.datastore.AbstractDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;

public abstract class AbstractArtificialDataStoreFromContig<T> extends AbstractDataStore<T>{

    private final DataStore<? extends Contig> contigs;
    /**
     * @param contig
     */
    public AbstractArtificialDataStoreFromContig(DataStore<? extends Contig> contigDataStore) {
        this.contigs = contigDataStore;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        super.contains(id);
        for(Contig contig : contigs){
            if(contig.containsPlacedRead(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public T get(String id) throws DataStoreException {
        super.get(id);
        for(Contig contig : contigs){
            if(contig.containsPlacedRead(id)){
                return createArtificalTypefor(contig.getPlacedReadById(id));
            }
        }
        return null;
    }

    protected abstract T createArtificalTypefor(PlacedRead read);
    @Override
    public Iterator<String> getIds() throws DataStoreException {
        super.getIds();
        List<Iterator<String>> iterators = new ArrayList<Iterator<String>>();
        for(Contig contig : contigs){
            iterators.add(new ContigReadIdIterator(contig));
        }
        return IteratorUtils.chainedIterator(iterators);
    }

    @Override
    public int size() throws DataStoreException {
        super.size();
        int size=0;
        for(Contig contig : contigs){
            size +=contig.getNumberOfReads();
        }
        return size;
    }

    private static class ContigReadIdIterator implements Iterator<String>{
        private final Iterator<PlacedRead> placedReadIterator;
        public ContigReadIdIterator(Contig contig){
            placedReadIterator = contig.getPlacedReads().iterator();
        }
        @Override
        public boolean hasNext() {
            return placedReadIterator.hasNext();
        }
        @Override
        public String next() {
            return placedReadIterator.next().getId();
        }
        @Override
        public void remove() {
            placedReadIterator.remove();
            
        }
    }
    
}
