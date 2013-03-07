package org.jcvi.jillion.assembly.ace;

import java.util.Date;

import org.jcvi.jillion.assembly.ctg.TigrContigReadVisitor;
import org.jcvi.jillion.assembly.ctg.TigrContigVisitor;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code AbstractAceAdaptedContigVisitor} is a {@link TigrContigVisitor}
 * that will build an {@link AceContig} from the visitXXX calls.
 * @author dkatzel
 *
 */
abstract class AbstractAceAdaptedContigVisitor implements TigrContigVisitor{

	 private AceContigBuilder contigBuilder;
	 private final Date phdDate;
	 private final QualitySequenceDataStore fullLengthQualityDataStore;
	 private final String contigId;
	 
	 
	 
    /**
     * Create a new AceAdapted Contig File DataStore using the given phdDate.
     * @param phdDate the date all faked phd files should be timestamped with.
     */
    public AbstractAceAdaptedContigVisitor(String contigId, QualitySequenceDataStore fullLengthFastXDataStore,Date phdDate) {
        this.phdDate = new Date(phdDate.getTime());
        this.fullLengthQualityDataStore = fullLengthFastXDataStore;
        this.contigId = contigId;
    }


	@Override
	public void visitConsensus(NucleotideSequence consensus) {
		contigBuilder = new AceContigBuilder(contigId, consensus);
		
	}

	@Override
	public TigrContigReadVisitor visitRead(final String readId,
			final long gappedStartOffset, final Direction dir,final Range validRange) {
		
		return new TigrContigReadVisitor(){

			@Override
			public void visitBasecalls(NucleotideSequence gappedBasecalls) {
				 PhdInfo phdInfo =new PhdInfo(readId, readId+".phd.1", phdDate);
				 int ungappedFullLength;
				try {
					ungappedFullLength = (int)fullLengthQualityDataStore.get(readId).getLength();
				} catch (DataStoreException e) {
					 throw new IllegalStateException("error getting full length trace for "+ readId);
				}
				contigBuilder.addRead(readId, gappedBasecalls, (int) gappedStartOffset, dir,
						validRange, phdInfo, ungappedFullLength);
				
			}

			@Override
			public void visitEnd() {
				//no-op				
			}
			
		};
	}

	@Override
	public void halted() {
		//no-op		
	}

	@Override
	public void visitEnd() {
		visitContig(contigBuilder);
		
	}

	protected abstract void visitContig(AceContigBuilder contigBuilder);
}
