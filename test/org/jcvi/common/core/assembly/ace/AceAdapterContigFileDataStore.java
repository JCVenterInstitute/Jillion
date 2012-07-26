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
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.assembly.ctg.ContigFileParser;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.FastXRecord;
import org.jcvi.common.core.util.iter.StreamingIterator;

public class AceAdapterContigFileDataStore extends AbstractAceAdaptedContigFileDataStore implements AceFileContigDataStore{

    private final Map<String, AceContig> map = new HashMap<String, AceContig>();
    private DataStore<AceContig> dataStore;
    private long totalNumberOfReads=0L;
    /**
     * @param phdDate
     */
    public AceAdapterContigFileDataStore(DataStore<? extends FastXRecord> fullLengthFastXDataStore,Date phdDate) {
        super(fullLengthFastXDataStore,phdDate);
    }
    public AceAdapterContigFileDataStore(DataStore<? extends FastXRecord> fullLengthFastXDataStore, Date phdDate, File contigFile) throws FileNotFoundException{
        this(fullLengthFastXDataStore,phdDate);
        ContigFileParser.parse(contigFile, this);
    }
    @Override
    protected void visitAceContig(AceContig aceContig) {
        map.put(aceContig.getId(), aceContig);  
        totalNumberOfReads += aceContig.getNumberOfReads();
    }

    @Override
    public void visitEndOfFile() {
        super.visitEndOfFile();
        dataStore = MapDataStoreAdapter.adapt(map);
        map.clear();
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return dataStore.contains(id);
    }

    @Override
    public AceContig get(String id) throws DataStoreException {
        return dataStore.get(id);
    }

    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return dataStore.idIterator();
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return dataStore.getNumberOfRecords();
    }

    @Override
    public void close() throws IOException {
        dataStore.close();
        
    }

    @Override
    public StreamingIterator<AceContig> iterator() throws DataStoreException {
        return dataStore.iterator();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return dataStore.isClosed();
    }
	@Override
	public long getNumberOfTotalReads() {
		return totalNumberOfReads;
	}
	@Override
	public StreamingIterator<WholeAssemblyAceTag> getWholeAssemblyTagIterator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public StreamingIterator<ReadAceTag> getReadTagIterator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public StreamingIterator<ConsensusAceTag> getConsensusTagIterator() {
		// TODO Auto-generated method stub
		return null;
	}

}
