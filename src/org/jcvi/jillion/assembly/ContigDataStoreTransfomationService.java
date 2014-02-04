package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public class ContigDataStoreTransfomationService {

	private final ContigDataStore<?,?> datastore;

	public ContigDataStoreTransfomationService(ContigDataStore<?, ?> datastore) {
		if(datastore ==null){
			throw new NullPointerException("datastore can not be null");
		}
		this.datastore = datastore;
	}
	
	
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
						//we could fake the raw sequence by ungapped and rev complement
						//but wouldn't be full length if there was upstream trimming.
						/*
						NucleotideSequenceBuilder rawSequenceBuilder = new NucleotideSequenceBuilder(read.getNucleotideSequence())
																			.ungap();
						if(read.getDirection() == Direction.REVERSE){
							rawSequenceBuilder.reverseComplement();
						}
						*/
						transformer.aligned(read.getId(), null, null, null, null, contigId,
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
	
	
}
