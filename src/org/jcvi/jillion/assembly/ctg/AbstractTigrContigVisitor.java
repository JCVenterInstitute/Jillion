package org.jcvi.jillion.assembly.ctg;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public abstract class AbstractTigrContigVisitor implements TigrContigVisitor{

	private final DataStore<Long> fullRangeLengthDataStore;
	
    private TigrContigBuilder currentContigBuilder;
    
    private final String contigId;
    
	public AbstractTigrContigVisitor(String contigId, DataStore<Long> fullLengthSequenceDataStore){
    	this.fullRangeLengthDataStore = fullLengthSequenceDataStore;
    	this.contigId = contigId;
    }

  

	@Override
	public void visitConsensus(NucleotideSequence consensus) {
		currentContigBuilder = new TigrContigBuilder(contigId, consensus);
		
	}

	@Override
	public TigrContigReadVisitor visitRead(final String readId,
			final long gappedStartOffset, final Direction dir) {
		return new TigrContigReadVisitor() {
			private NucleotideSequence gappedBasecalls;
			private Range validRange;
			@Override
			public void visitValidRange(Range validRange) {
				this.validRange = validRange;				
			}
			
			@Override
			public void visitIncompleteEnd() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visitEnd() {
				final Long fullLength;
				try {
					fullLength = fullRangeLengthDataStore.get(readId);
				} catch (DataStoreException e) {
					throw new IllegalStateException("error reading from full length sequence datastore for read "+ readId, e);
				}
				if(fullLength ==null){
					throw new IllegalStateException("full length sequence datastore did not contain read "+ readId);
				}
				currentContigBuilder.addRead(readId, (int)gappedStartOffset, validRange, 
						gappedBasecalls.toString(), dir, fullLength.intValue());
				
				
			}
			
			@Override
			public void visitBasecalls(NucleotideSequence gappedBasecalls) {
				this.gappedBasecalls = gappedBasecalls;				
			}
		};
	}

	@Override
	public void visitIncompleteEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitEnd() {
		TigrContig contig = currentContigBuilder.build();
		currentContigBuilder=null;
		visitContig(contig);
	}
    
	protected abstract void visitContig(TigrContig contig);
    
}
