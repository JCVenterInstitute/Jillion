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
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.trace.fastq.CommentedParsedFastqRecord;
import org.jcvi.jillion.internal.trace.fastq.ParsedFastqRecord;
/**
 * A {@link PairedFastqRecordVisitor} that will visit
 * both the read1 and read2 {@link FastqRecord}s of a pair.
 * @author dkatzel
 *
 * @since 6.0.2
 */
public abstract class AbstractPairedFastqRecordVisitor implements PairedFastqRecordVisitor{

	private final String read1Id;
	private final String read1OptionalComment;
	private final String read2Id;
	private final String read2OptionalComment;
	private final FastqQualityCodec qualityCodec;
	
	private String currentBasecalls;
	private QualitySequence currentQualities;
	private String encodedQualities;
	
	
	private String currentRead2Basecalls;
	private QualitySequence currentRead2Qualities;
	private String encodedRead2Qualities;
	
	private boolean turnOffCompression;
	
	public AbstractPairedFastqRecordVisitor(String read1Id, String read1OptionalComment,
			String read2Id, String read2OptionalComment,
			FastqQualityCodec qualityCodec){
		this(read1Id, read1OptionalComment, read2Id, read2OptionalComment, qualityCodec, false);
	}
	public AbstractPairedFastqRecordVisitor(String read1Id, String read1OptionalComment,
			String read2Id, String read2OptionalComment,
			FastqQualityCodec qualityCodec, boolean turnOffCompression) {
		this.read1Id = read1Id;
		this.read1OptionalComment = read1OptionalComment;
		this.read2Id = read2Id;
		this.read2OptionalComment = read2OptionalComment;
		
		this.qualityCodec = qualityCodec;
		this.turnOffCompression = turnOffCompression;
	}

	@Override
	public void visitRead1Nucleotides(String nucleotides) {
		currentBasecalls = nucleotides;
		
	}
	@Override
	public void visitRead2Nucleotides(String nucleotides) {
		currentRead2Basecalls = nucleotides;
		
	}
	@Override
	public void visitEncodedRead1Qualities(String encodedQualities) {
		this.encodedQualities = encodedQualities;
		
	}
	@Override
	public void visitRead1Qualities(QualitySequence qualities) {
		currentQualities = qualities;
		
	}
	@Override
	public void visitEncodedRead2Qualities(String encodedQualities) {
		this.encodedRead2Qualities = encodedQualities;
		
	}
	@Override
	public void visitRead2Qualities(QualitySequence qualities) {
		currentRead2Qualities = qualities;
		
	}
	

	@Override
	public final void visitEnd() {
	    FastqRecord fastqRecord;
	    
	    if(currentQualities ==null){
	    	if(read1OptionalComment ==null){
	    		fastqRecord = new ParsedFastqRecord(read1Id, currentBasecalls , encodedQualities, qualityCodec, turnOffCompression);
	        }else{
	            fastqRecord = new CommentedParsedFastqRecord(read1Id, currentBasecalls , encodedQualities, qualityCodec, turnOffCompression, read1OptionalComment);
	        }       	
	    }else{
	    	 fastqRecord = FastqRecordBuilder.create(read1Id, new NucleotideSequenceBuilder(currentBasecalls)
															.turnOffDataCompression(turnOffCompression)
															.build(), 
													currentQualities)
											.comment(read1OptionalComment)
											.build();
	    }
	    
	    FastqRecord fastq2Record;
	    
	    if(currentRead2Qualities ==null){
	    	if(read2OptionalComment ==null){
	    		fastq2Record = new ParsedFastqRecord(read2Id, currentRead2Basecalls , encodedRead2Qualities, qualityCodec, turnOffCompression);
	        }else{
	            fastq2Record = new CommentedParsedFastqRecord(read2Id, currentRead2Basecalls , encodedRead2Qualities, qualityCodec, turnOffCompression, read2OptionalComment);
	        }       	
	    }else{
	    	 fastq2Record = FastqRecordBuilder.create(read2Id, new NucleotideSequenceBuilder(currentRead2Basecalls)
															.turnOffDataCompression(turnOffCompression)
															.build(), 
													currentRead2Qualities)
											.comment(read2OptionalComment)
											.build();
	    }
	    
	    visitRecordPair(fastqRecord, fastq2Record);
	}
	
	@Override
	public void halted() {
		//no-op			
	}

	protected abstract void visitRecordPair(FastqRecord read1Record, FastqRecord read2Record);
	
	

}
