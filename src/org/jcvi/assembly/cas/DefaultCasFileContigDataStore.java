/*
 * Created on Dec 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.assembly.cas.alignment.CasAlignment;
import org.jcvi.assembly.cas.alignment.CasAlignmentRegion;
import org.jcvi.assembly.cas.alignment.CasAlignmentRegionType;
import org.jcvi.assembly.cas.read.CasPlacedRead;
import org.jcvi.assembly.cas.read.DefaultCasPlacedRead;
import org.jcvi.assembly.cas.read.DefaultCasPlacedReadFromCasAlignmentBuilder;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class DefaultCasFileContigDataStore extends AbstractOnePassCasFileVisitor implements CasContigDataStore<CasContig>{

    private final Map<Long, DefaultCasContig.Builder> contigBuilderMap = new HashMap<Long, DefaultCasContig.Builder>();
    private DataStore<CasContig> datastore;
    private final CasIdLookup referenceIdLookup;
    private final CasIdLookup readIdLookup;
    private final CasGappedReferenceMap gappedReferenceMap;
    private long readCounter =0;
    private final DataStore<NucleotideEncodedGlyphs> nucleotideDataStore;
    
    
    
    /**
     * @param referenceIdLookup
     * @param readIdLookup
     * @param gappedReferenceMap
     * @param nucleotideDataStore
     * @param qualityDataStore
     * @param consensusCaller
     * @param qualityValueStrategy
     */
    public DefaultCasFileContigDataStore(CasIdLookup referenceIdLookup,
            CasIdLookup readIdLookup, CasGappedReferenceMap gappedReferenceMap,
            DataStore<NucleotideEncodedGlyphs> nucleotideDataStore) {
        this.referenceIdLookup = referenceIdLookup;
        this.readIdLookup = readIdLookup;
        this.gappedReferenceMap = gappedReferenceMap;
        this.nucleotideDataStore = nucleotideDataStore;
    }

    @Override
    public synchronized void visitMatch(CasMatch match) {
        super.visitMatch(match);
        for(CasAlignment alignment :match.getAlignments()){
            long referenceId = alignment.contigSequenceId();
            String readId = readIdLookup.getLookupIdFor(readCounter);

            DefaultCasPlacedReadFromCasAlignmentBuilder builder;
            long ungappedStartOffset = alignment.getStartOfMatch();
            final NucleotideEncodedGlyphs gappedReference = gappedReferenceMap.getGappedReferenceFor(referenceId);
            long gappedStartOffset = gappedReference.convertUngappedValidRangeIndexToGappedValidRangeIndex((int)ungappedStartOffset);
            try {
                builder = new DefaultCasPlacedReadFromCasAlignmentBuilder(readId,
                        nucleotideDataStore.get(readId),
                        alignment.readIsReversed(),
                        gappedStartOffset
                       );
                List<CasAlignmentRegion> regionsToConsider = new ArrayList<CasAlignmentRegion>(alignment.getAlignmentRegions());
                int lastIndex = regionsToConsider.size()-1;
                if(regionsToConsider.get(lastIndex).getType()==CasAlignmentRegionType.INSERT){
                    regionsToConsider.remove(lastIndex);
                }
                builder.addAlignmentRegions(regionsToConsider,gappedReference);
                
                
                final DefaultCasPlacedRead casPlacedRead = builder.build();
                visitPlacedRead(referenceId,casPlacedRead);
                contigBuilderMap.get(referenceId).addCasPlacedRead(casPlacedRead);
            } catch (DataStoreException e) {
                throw new IllegalStateException("could not create read placement for "+ alignment, e);
            }
        }
        readCounter++;
    }

    protected void visitPlacedRead(long referenceId, CasPlacedRead casPlacedRead){
        if(!contigBuilderMap.containsKey(referenceId)){
            contigBuilderMap.put(referenceId, new DefaultCasContig.Builder(
                    referenceIdLookup.getLookupIdFor(referenceId)) );
        }
        contigBuilderMap.get(referenceId).addCasPlacedRead(casPlacedRead);
       
    }
    

    @Override
    public synchronized void visitEndOfFile() {
        super.visitEndOfFile();
        System.out.println("end of cas file");
        Map<String, CasContig> map = new HashMap<String, CasContig>(contigBuilderMap.size());
        for(Entry<Long, DefaultCasContig.Builder> entry : contigBuilderMap.entrySet()){
            String contigId = referenceIdLookup.getLookupIdFor(entry.getKey());
            System.out.println(contigId);
            map.put(contigId, entry.getValue().build());
        }
        datastore = new SimpleDataStore<CasContig>(map);
    }

    public CasIdLookup getReferenceIdLookup() {
        return referenceIdLookup;
    }

    public CasIdLookup getReadIdLookup() {
        return readIdLookup;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }

    @Override
    public CasContig get(String id) throws DataStoreException {
        return datastore.get(id);
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return datastore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return datastore.size();
    }

    @Override
    public void close() throws IOException {
       datastore.close();
        
    }

    @Override
    public Iterator<CasContig> iterator() {
        return datastore.iterator();
    }

}
