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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.util.Date;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public abstract class  AbstractTasmContigBuilderVisitor implements TasmContigVisitor{

	private TasmContigBuilder builder;
	private final String contigId;
	private final DataStore<Long> fullRangeLengthDataStore;
	
	public AbstractTasmContigBuilderVisitor(String contigId, DataStore<Long> fullRangeLengthDataStore){
		this.contigId = contigId;
		this.fullRangeLengthDataStore = fullRangeLengthDataStore;
	}
	
	@Override
	public void visitConsensus(NucleotideSequence consensus) {
		builder = new TasmContigBuilder(contigId, consensus);
	}

	@Override
	public void visitCeleraId(long id) {
		builder.withCeleraAssemblerContigId(Long.valueOf(id));		
	}

	@Override
	public void visitComments(Integer bacId, String comment, String commonName,
			String assemblyMethod, boolean isCircular) {
		if(bacId !=null){
			builder.setSampleId(bacId.toString());
		}
		if(commonName !=null){
			builder.withCommonName(commonName);
		}
		builder.withMethod(assemblyMethod);
		builder.isCircular(isCircular);
	}

	@Override
	public void visitCoverageData(int numberOfReads, float avgCoverage) {
		//2013-05-31
		//Explicitly set the avg coverage and num reads
		//in case this is an annotation tasm
		//if we see any reads later, we will null these out
		builder.setCoverageInfo(numberOfReads, Double.valueOf(avgCoverage));
	}

	@Override
	public void visitLastEdited(String username, Date editDate) {
		builder.withEditInfo(username, editDate);
	}

	@Override
	public TasmContigReadVisitor visitRead(final String readId,
			final long gappedStartOffset, final Direction dir, final Range validRange) {
		//2013-05-31
		//we have reads
		//so we are NOT an annotation contig
		//clear out set values so they are correctly computed
		builder.setCoverageInfo(null,null);
		return new TasmContigReadVisitor() {
			private NucleotideSequence gappedBasecalls;
			
			
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
				String gappedSequence = gappedBasecalls.toString();
				builder.addRead(readId, (int)gappedStartOffset, validRange, 
						gappedSequence, dir, fullLength.intValue());
				
			}
			
			@Override
			public void visitBasecalls(NucleotideSequence gappedBasecalls) {
				this.gappedBasecalls = gappedBasecalls;				
			}
		};
	}

	@Override
	public void halted() {
		//no-op
		
	}

	@Override
	public void visitEnd() {
		visitRecord(builder);
		builder=null;
		
	}
	
	

	/**
	 * Visit a populated {@link TasmContigBuilder} instance
	 * that has been created using the data from a single 
	 * contig record in a tasm file.
	 * @param builder a {@link TasmContigBuilder} instance;
	 * never null.
	 */
	protected abstract void visitRecord(TasmContigBuilder builder);

}
