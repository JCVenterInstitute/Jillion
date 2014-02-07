package org.jcvi.jillion.assembly;

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
		try{
			contigIter = datastore.iterator();
			while(contigIter.hasNext()){
				Contig<?> contig = contigIter.next();
				String contigId = contig.getId();
				transformer.referenceOrConsensus(contigId, contig.getConsensusSequence());
				StreamingIterator<? extends AssembledRead> readIter =contig.getReadIterator();
				try{
					while(readIter.hasNext()){
						AssembledRead read = readIter.next();
						
						NucleotideSequence rawSeq = rawSequences ==null ? null : rawSequences.get(read.getId());
						QualitySequence rawQual = rawQualities ==null ? null : rawQualities.get(read.getId());
						
						transformer.aligned(read.getId(), rawSeq, rawQual, null, null, contigId,
								(int) read.getGappedStartOffset(), 
								read.getDirection(), read.getNucleotideSequence(), read.getReadInfo());
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
