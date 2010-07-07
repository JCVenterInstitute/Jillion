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
 * Created on Dec 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.jcvi.assembly.cas.read.CasPlacedRead;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class DefaultCasFileContigDataStore extends AbstractCasFileContigVisitor implements CasContigDataStore<CasContig>{

    private final Map<Long, DefaultCasContig.Builder> contigBuilderMap = new HashMap<Long, DefaultCasContig.Builder>();
    private DataStore<CasContig> datastore;
    
 

    /**
     * @param referenceIdLookup
     * @param readIdLookup
     * @param gappedReferenceMap
     * @param nucleotideDataStore
     * @param trimDataStore
     */
    public DefaultCasFileContigDataStore(CasIdLookup referenceIdLookup,
            CasIdLookup readIdLookup, CasGappedReferenceMap gappedReferenceMap,
            DataStore<NucleotideEncodedGlyphs> nucleotideDataStore,
            TrimDataStore trimDataStore) {
        super(referenceIdLookup, readIdLookup, gappedReferenceMap, nucleotideDataStore,
                trimDataStore);
    }

   
    @Override
    protected void visitPlacedRead(long referenceId, CasPlacedRead casPlacedRead){
        if(!contigBuilderMap.containsKey(referenceId)){
            contigBuilderMap.put(referenceId, new DefaultCasContig.Builder(
                    getReferenceIdLookup().getLookupIdFor(referenceId)) );
        }
        contigBuilderMap.get(referenceId).addCasPlacedRead(casPlacedRead);
       
    }
    

    @Override
    public synchronized void visitEndOfFile() {
        super.visitEndOfFile();
        System.out.println("end of cas file");
        Map<String, CasContig> map = new HashMap<String, CasContig>(contigBuilderMap.size());
        for(Entry<Long, DefaultCasContig.Builder> entry : contigBuilderMap.entrySet()){
            String contigId = getReferenceIdLookup().getLookupIdFor(entry.getKey());
            System.out.println(contigId);
            map.put(contigId, entry.getValue().build());
        }
        datastore = new SimpleDataStore<CasContig>(map);
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
