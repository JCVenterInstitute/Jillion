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
package org.jcvi.jillion.assembly.consed.ace;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback.AceFileVisitorMemento;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public class AceTestUtil {

	private AceTestUtil(){
		//can not instantiate
	}
	public static AceParser createAceHandlerFor(final AceContig contig){
		return new SingleContigAceHandler(contig);
	}
	
	
	
	private static class SingleContigAceHandler implements AceParser{

		private final AceContig contig;
		
		public SingleContigAceHandler(AceContig contig) {
			this.contig = contig;
		}

		@Override
		public void parse(AceFileVisitor visitor) throws IOException {
			if(visitor==null){
				throw new NullPointerException("visitor can not be null");
			}
			visitor.visitHeader(1, contig.getNumberOfReads());
			AceObjectVisitorCallback callback = new AceObjectVisitorCallback(contig.getId());
			new SingleContigHandler(contig, callback).accept(visitor);
			if(callback.keepParsing.get()){
				visitor.visitEnd();
			}else{
				visitor.halted();
			}
			
			
		}

		@Override
		public void parse(AceFileVisitor visitor,
				AceFileVisitorMemento memento) throws IOException {
			if(!(memento instanceof Memento)){
				throw new IllegalArgumentException("invalid memento type");
			}
			String contigIdToParse = ((Memento)memento).contigId;
			if(contig.getId().equals(contigIdToParse)){
				parse(visitor);
			}else{
				throw new IllegalArgumentException("unknown contig id "+ contigIdToParse);
			}
		}
		
		private class AceObjectVisitorCallback implements AceFileVisitorCallback{
			private final String contigId;
			private final AtomicBoolean keepParsing = new AtomicBoolean(true);
			
			public AceObjectVisitorCallback(String contigId) {
				this.contigId = contigId;
			}

			@Override
			public boolean canCreateMemento() {
				return true;
			}

			@Override
			public AceFileVisitorMemento createMemento() {
				return new Memento(contigId);
			}

			@Override
			public void haltParsing() {
				keepParsing.set(false);
				
			}
			
		}

		@Override
		public boolean canParse() {
			return true;
		}
		
		private static class Memento implements AceFileVisitorMemento{
			private final String contigId;

			public Memento(String contigId) {
				this.contigId = contigId;
			}
			
		}
	};
	
	private static class SingleContigHandler {
		private final AceContig contig;
		private final org.jcvi.jillion.assembly.consed.ace.AceTestUtil.SingleContigAceHandler.AceObjectVisitorCallback callback;
		
		public SingleContigHandler(AceContig contig, org.jcvi.jillion.assembly.consed.ace.AceTestUtil.SingleContigAceHandler.AceObjectVisitorCallback callback) {
			this.contig = contig;
			this.callback = callback;
		}
		
		public void accept(AceFileVisitor visitor){
			NucleotideSequence consensus = contig.getConsensusSequence();
			long numberOfReads = contig.getNumberOfReads();
		 
			AceContigVisitor contigVisitor =visitor.visitContig(callback, contig.getId(),(int)consensus.getLength(), (int)numberOfReads, 0, contig.isComplemented());
			if(contigVisitor !=null){
				contigVisitor.visitBasesLine(consensus.toString());
				StreamingIterator<AceAssembledRead> readIter =contig.getReadIterator();
				while(callback.keepParsing.get() && readIter.hasNext()){
					AceAssembledRead read = readIter.next();
					Range validRange =read.getReadInfo().getValidRange();
					final int afStart;
					if(read.getDirection()==Direction.FORWARD){
						afStart = (int)read.getGappedStartOffset() - (int)validRange.getBegin()+1;
					}else{
						afStart = (int)read.getGappedStartOffset() - (read.getReadInfo().getUngappedFullLength() -((int)validRange.getEnd()+1)) +1;
					}
					contigVisitor.visitAlignedReadInfo(read.getId(), read.getDirection(), afStart);
					AceContigReadVisitor readVisitor =contigVisitor.visitBeginRead(read.getId(), (int)read.getGappedLength());
					if(readVisitor !=null){
						
						//need to build fullLength sequence with N's outside of valid range
						NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(read.getNucleotideSequence());
						String leftNs = createStringOfNs(validRange.getBegin());
						String rightNs = createStringOfNs(read.getReadInfo().getUngappedFullLength() -(validRange.getEnd()+1));
						final Range.Builder alignRange;
						if(read.getDirection()==Direction.FORWARD){
							builder.prepend(leftNs);
							builder.append(rightNs);
							alignRange = new Range.Builder(validRange);
						}else{
							builder.prepend(rightNs);
							builder.append(leftNs);
							alignRange = new Range.Builder(AssemblyUtil.reverseComplementValidRange(validRange, read.getReadInfo().getUngappedFullLength()));
						}
						if(callback.keepParsing.get()){
							readVisitor.visitBasesLine(builder.toString());
						}
						if(callback.keepParsing.get()){		
							//alignment range include Gaps!
							alignRange.expandEnd(read.getNucleotideSequence().getNumberOfGaps());
							Range gappedAlignRange = alignRange.build();
							readVisitor.visitQualityLine((int)gappedAlignRange.getBegin()+1, (int)gappedAlignRange.getEnd()+1, 
													(int)gappedAlignRange.getBegin()+1, (int)gappedAlignRange.getEnd()+1);
						}
						if(callback.keepParsing.get()){
							PhdInfo phdInfo =read.getPhdInfo();
							
							readVisitor.visitTraceDescriptionLine(phdInfo.getTraceName(), phdInfo.getPhdName(), phdInfo.getPhdDate());
						}
						if(callback.keepParsing.get()){
							readVisitor.visitEnd();
						}else{
							readVisitor.halted();
						}
					}
				}
				if(callback.keepParsing.get()){
					contigVisitor.visitEnd();
				}else{
					contigVisitor.halted();
				}
			}
		}
	}


	private static String createStringOfNs(long length) {
		
		return new String(new char[(int)length]).replace('\0', 'N');
	}
}
