/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileParser;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileVisitor;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigVisitor;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.qual.QualityFastaDataStore;

public class AceAdapterContigFileDataStore implements AceFileDataStore{

    private final Map<String, AceContig> map = new HashMap<String, AceContig>();
    private DataStore<AceContig> dataStore;
    private long totalNumberOfReads=0L;

    public static AceFileDataStore create(final QualityFastaDataStore fullLengthQualityDataStore, final Date phdDate, File contigFile) throws IOException{
        return create(fullLengthQualityDataStore, phdDate, contigFile,false);
    }
    public static AceFileDataStore create(final QualityFastaDataStore fullLengthQualityDataStore, final Date phdDate, File contigFile, final boolean computeConsedConsensusQualities) throws IOException{
    	final AceAdapterContigFileDataStore datastore = new AceAdapterContigFileDataStore();
    	TigrContigFileVisitor visitor =new TigrContigFileVisitor() {
			
			@Override
			public void halted() {
				//no-op
				
			}
			
			@Override
			public void visitEnd() {
				datastore.dataStore = DataStore.of(datastore.map);
			    datastore.map.clear();
				
			}
			
			@Override
			public TigrContigVisitor visitContig(TigrContigVisitorCallback callback,
					String contigId) {
				final QualitySequenceDataStore qualitySequences =fullLengthQualityDataStore.asSequenceDataStore();
				
				return new AbstractAceAdaptedContigVisitor(contigId,qualitySequences, phdDate) {
					
					@Override
					protected void visitContig(AceContigBuilder contigBuilder) {
						if(computeConsedConsensusQualities){
							contigBuilder.computeConsensusQualities(qualitySequences);
						}
						AceContig contig = contigBuilder.build();
						datastore.map.put(contig.getId(), contig);  
						datastore.totalNumberOfReads += contig.getNumberOfReads();
						
					}
				};
			}
		};
		
		TigrContigFileParser.create(contigFile).parse(visitor);
		return datastore;
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
    
    
    @Override
	public StreamingIterator<DataStoreEntry<AceContig>> entryIterator()
			throws DataStoreException {
		return dataStore.entryIterator();
	}
	/**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() {
        return dataStore.isClosed();
    }
	@Override
	public long getNumberOfTotalReads() {
		return totalNumberOfReads;
	}
	@Override
	public StreamingIterator<WholeAssemblyAceTag> getWholeAssemblyTagIterator() {
		return IteratorUtil.createEmptyStreamingIterator();
	}
	@Override
	public StreamingIterator<ReadAceTag> getReadTagIterator() {
		return IteratorUtil.createEmptyStreamingIterator();
	}
	@Override
	public StreamingIterator<ConsensusAceTag> getConsensusTagIterator() {
		return IteratorUtil.createEmptyStreamingIterator();
	}

}
