/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.assembly.consed.ConsedUtil.ClipPointsType;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.MapUtil;
/**
 * {@code AbstractAceContigBuilderVisitor} is an {@link AceContigVisitor}
 * that will create a {@link AceContigBuilder} and populate it using the
 * visit calls. 
 * @author dkatzel
 *
 */
public abstract class AbstractAceContigBuilderVisitor implements AceContigVisitor{

	private final String contigId;
	private final NucleotideSequenceBuilder consensusBuilder;
	private AceContigBuilder builder;
	private final Map<String, AlignedReadInfo> currentAlignedReadInfoMap;
	/**
	 * Create a new instance of {@link AbstractAceContigBuilderVisitor} that will
	 * populate an {@link AceContigBuilder} as visit methods are called.
	 * @param contigId the contig id of the contig to build.
	 * @param consensusLength the expected initial consensus length; must be >1.
	 * @param numberOfReads the expected initial number of reads that will be in this builder;
	 * must be >=0.  This value is only used to initialize the size of internal hashes,
	 * the number of reads actually visited may be greater or less than this value.
	 * @throws IllegalArgumentException if consensusLength <1 or numberOfReads <0.
	 */
	public AbstractAceContigBuilderVisitor(String contigId, int consensusLength, int numberOfReads) {
		this.contigId = contigId;
		consensusBuilder = new NucleotideSequenceBuilder(consensusLength);
		int mapCapacity = MapUtil.computeMinHashMapSizeWithoutRehashing(numberOfReads);
		currentAlignedReadInfoMap = new HashMap<String, AlignedReadInfo>(mapCapacity);
	}

	@Override
	public final void visitBasesLine(String mixedCaseBasecalls) {
		consensusBuilder.append(mixedCaseBasecalls);
		
	}
	/**
	 * Ignored by default, users may override this method if they wish.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	public void visitConsensusQualities(
			QualitySequence ungappedConsensusQualities) {
		//no-op ? do we care?		
	}

	@Override
	public final void visitAlignedReadInfo(String readId, Direction dir,
			int gappedStartOffset) {		
		createContigBuilderIfNeeded();
		
		final AlignedReadInfo alignedInfo = new AlignedReadInfo(gappedStartOffset, dir);
        currentAlignedReadInfoMap.put(readId, alignedInfo);
		
	}

	private void createContigBuilderIfNeeded() {
		if(builder ==null){
			builder = new AceContigBuilder(contigId, consensusBuilder.build(), currentAlignedReadInfoMap.size());
		}
	}
	/**
	 * Ignored by default, users may override this method if they wish.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	public void visitBaseSegment(Range gappedConsensusRange, String readId) {
		//no-op		
	}
	/**
	 * This method is only called if a read in this contig
	 * being visited contains invalid data so that the read 
	 * can not be added to the contig builder.  The most common
	 * reason why a read is ignored is the read does not 
	 * contain any high quality bases that aligned to
	 * the consensus.
	 * <p/>
	 * By default this method does not do anything,
	 * users are encouraged to implement this method
	 * to perform some kind of logging.
	 * @param readId the read id that will be ignored
	 * by the contig builder.
	 * @param reason the reason why the read will be ignored.
	 */
	protected void readIgnored(String readId, String reason){
		//no-op
	}

	@Override
	public final AceContigReadVisitor visitBeginRead(String readId, int gappedLength) {
		return new ReadVisitor(readId, gappedLength, currentAlignedReadInfoMap.get(readId));
	}

	@Override
	public final void visitEnd() {
		//create new builder incase
		//contig doesn't have any reads
		createContigBuilderIfNeeded();
		visitContig(builder);
		
	}
	/**
     * The entire contig has been visited.  This method
     * will only be called from inside {@link #visitEnd()}.
     * Subclasses may modify the builder as they see fit.
     * @param builder a completely populated {@link AceContigBuilder}
     * instance containing all the contig data gathered from
     * the visit methods; will never be null.
     */
	protected abstract void visitContig(AceContigBuilder builder);
	/**
	 * By default does nothing, users may override this method
	 * to handle a halted visitor.
	 */
	@Override
	public void halted() {
		//no-op		
	}
	/**
	 * {@code ReadVisitor} is a  {@link AceContigReadVisitor}
	 * that will collect all the visit data for a single read
	 * to create an {@link AceAssembledRead} to add to our
	 * {@link AceContigBuilder}.  The read is added
	 * to our builder during the {@link AceContigReadVisitor#visitEnd()}
	 * call.  If the read contains invalid data then the read is 
	 * not added to the builder and the method 
	 * {@link AbstractAceContigBuilderVisitor#readIgnored(String, String)}}
	 * is called passing the read id along with a string explaining the reason
	 * why this read was ignored.
	 * @author dkatzel
	 *
	 */
	private final class ReadVisitor implements AceContigReadVisitor{
		private final String readId;
		private final AlignedReadInfo alignedInfo;
		private final NucleotideSequenceBuilder fullLengthSequenceBuilder;
		private boolean skipCurrentRead =false;
		private NucleotideSequence validSequence;
		private int ungappedFullLength;
		
		private int currentOffset;
		private Range currentClearRange;
		
		private PhdInfo currentPhdInfo;
		
		
		public ReadVisitor(String readId, int fullGappedLength,AlignedReadInfo alignedInfo){
			this.readId = readId;
			this.alignedInfo = alignedInfo;
			fullLengthSequenceBuilder = new NucleotideSequenceBuilder(fullGappedLength);			
		}
		
		@Override
		public void visitQualityLine(int qualLeft, int qualRight,
				int alignLeft, int alignRight) {
			ClipPointsType clipPointsType = ConsedUtil.ClipPointsType.getType(qualLeft, qualRight, alignLeft, alignRight);
			if(clipPointsType != ClipPointsType.VALID){
				handleInvalidRead(qualLeft, qualRight, clipPointsType);
				return;
			}
			//dkatzel 4/2011 - There have been cases when qual coords and align coords
	        //do not match; usually qual is a sub set of align
	        //but occasionally, qual goes beyond the align coords.
	        //I guess this happens in a referenced based alignment for
	        //reads at the edges when the reads have good quality 
	        //beyond the reference.
	        //It might also be possible that the read has been 
	        //edited and that could have changed the coordinates.
	        //Therefore intersect the qual and align coords
	        //to find the region we are interested in
	        Range qualityRange = Range.of(CoordinateSystem.RESIDUE_BASED, qualLeft,qualRight);
	        Range alignmentRange = Range.of(CoordinateSystem.RESIDUE_BASED, alignLeft,alignRight);
	        Range gappedValidRange =qualityRange.intersection(alignmentRange);
	     
	        currentOffset = computeReadOffset(gappedValidRange.getBegin(CoordinateSystem.RESIDUE_BASED));            
	        
	       
	        //this will set currentValidBasecalls to only be the valid range
	        validSequence =  fullLengthSequenceBuilder.copy().trim(gappedValidRange)
          						.build();
	        NucleotideSequence gappedFullLengthSequence = fullLengthSequenceBuilder.build();
          final int numberOfFullLengthGaps = gappedFullLengthSequence.getNumberOfGaps();
          ungappedFullLength = (int) gappedFullLengthSequence.getLength() - numberOfFullLengthGaps;
          //dkatzel 2011-11-18
          //It is possible that there are gaps outside of the valid
          //range (maybe from editing the ace in consed?)
          //we need to account for that
          //the one problem is that this could cause minor
          //differences if we then re-write the ace since
          //we will lose the gaps outside of the valid range
          //but that won't affect real assembly data
          //it will only show up if both versions (before and after)
          //of the file were diff'ed.
          int ungappedClearLeft = gappedFullLengthSequence.getUngappedOffsetFor((int)gappedValidRange.getBegin());
          int ungappedClearRight = gappedFullLengthSequence.getUngappedOffsetFor((int)gappedValidRange.getEnd());
          Range ungappedValidRange = Range.of(CoordinateSystem.RESIDUE_BASED, ungappedClearLeft+1, ungappedClearRight+1 );
          if(alignedInfo.getDirection() == Direction.REVERSE){
              ungappedValidRange = AssemblyUtil.reverseComplementValidRange(ungappedValidRange, ungappedFullLength);            
          }
          currentClearRange = ungappedValidRange;
			
		}
		private void handleInvalidRead(int qualLeft, int qualRight,
				ClipPointsType clipPointsType) {
			skipCurrentRead = true;
			switch(clipPointsType){
				case NEGATIVE_VALID_RANGE:					
					readIgnored(readId, String.format("has a negative valid range %d%n",
			                    (qualRight-qualLeft)));
					 break;
				case ALL_LOW_QUALITY :
					readIgnored(readId, "entire read is low quality");
			        break;
				case NO_HIGH_QUALITY_ALIGNMENT_INTERSECTION:
					readIgnored(readId, "read does not have a high quality aligned range");
			    	break;
				default: throw new IllegalStateException("unknown clipPointType "+ clipPointsType);
			}				       
		}

		private int computeReadOffset(long startPosition) {
			return alignedInfo.getStartOffset() + (int) startPosition - 2;
		}
		@Override
		public void visitTraceDescriptionLine(String traceName, String phdName,
				Date date) {
			currentPhdInfo =new PhdInfo(traceName, phdName, date);			
		}

		@Override
		public void visitBasesLine(String mixedCaseBasecalls) {
			fullLengthSequenceBuilder.append(mixedCaseBasecalls);
			
		}

		@Override
		public void visitEnd() {
			if(skipCurrentRead){
				return;
			}
			if(validSequence ==null){
				throw new IllegalStateException(String.format("Incomplete visitation : did not visit quality line for read %s",readId));
			}
			if(currentPhdInfo ==null){
				throw new IllegalStateException(String.format("Incomplete visitation : did not visit trace line for read %s",readId));
			}
			builder.addRead(readId, validSequence, currentOffset, alignedInfo.getDirection(), 
					currentClearRange, currentPhdInfo, ungappedFullLength);
		}

		@Override
		public void halted() {
			//no-op			
		}
		
	}
}
