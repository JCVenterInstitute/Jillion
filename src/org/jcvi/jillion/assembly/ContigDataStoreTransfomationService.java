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
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.assembly.AssemblyTransformer.AssemblyTransformerCallback;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public final class ContigDataStoreTransfomationService implements AssemblyTransformationService{

	private final ContigDataStore<?,?> datastore;
	private final NucleotideSequenceDataStore rawSequences;
	private final QualitySequenceDataStore rawQualities;
	
	private ContigDataStoreTransfomationService(Builder builder) {
	
		this.datastore = builder.datastore;
		this.rawQualities = builder.rawQualities;
		this.rawSequences = builder.rawSequences;
	}
	
	@Override
	public void transform(AssemblyTransformer transformer){
		if(transformer ==null){
			throw new NullPointerException("transformer can not be null");
		}
		
		StreamingIterator<? extends Contig<?>> contigIter = null;
		boolean[] halt = new boolean[1];
		
		AssemblyTransformerCallback callback = ()->{
			halt[0]=true;
		};
		try{
			contigIter = datastore.iterator();
			while(!halt[0] && contigIter.hasNext()){
				Contig<?> contig = contigIter.next();
				String contigId = contig.getId();
				transformer.referenceOrConsensus(contigId, contig.getConsensusSequence(), callback);
				if(halt[0]) {
					continue;
				}
				StreamingIterator<? extends AssembledRead> readIter =contig.getReadIterator();
				try{
					while(readIter.hasNext()){
						AssembledRead read = readIter.next();
						
						NucleotideSequence rawSeq = rawSequences ==null ? null : rawSequences.get(read.getId());
						QualitySequence rawQual = rawQualities ==null ? null : rawQualities.get(read.getId());
						
						transformer.aligned(read.getId(), rawSeq, rawQual, null, null, contigId,
								(int) read.getGappedStartOffset(), 
								read.getDirection(), read.getNucleotideSequence(), read.getReadInfo(), read);
					}
				}finally{
					IOUtil.closeAndIgnoreErrors(readIter);
				}
			}
			transformer.endAssembly();
		} catch (DataStoreException e) {
			throw new IllegalStateException("error reading datastore",e);
		}finally{
			IOUtil.closeAndIgnoreErrors(contigIter);
		}
	}
	
	public static final class Builder{
		private final ContigDataStore<?,?> datastore;

		private NucleotideSequenceDataStore rawSequences;
		private QualitySequenceDataStore rawQualities;
		//private Position
		
		public Builder(ContigDataStore<?, ?> datastore) {
			if(datastore ==null){
				throw new NullPointerException("datastore can not be null");
			}
			this.datastore = datastore;
		}
		
		public Builder setRawSequenceDataStore(NucleotideSequenceDataStore datastore){
			this.rawSequences = datastore;
			return this;
		}
		
		public Builder setRawQualitiyDataStore(QualitySequenceDataStore datastore){
			this.rawQualities = datastore;
			return this;
		}
		
		
		
		public ContigDataStoreTransfomationService build(){
			return new ContigDataStoreTransfomationService(this);
		}
		
	}
	
	
}
