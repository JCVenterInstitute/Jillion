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

	private final String id;
	private final String optionalComment;
	private final FastqQualityCodec qualityCodec;
	
	private String currentBasecalls;
	private QualitySequence currentQualities;
	private String encodedQualities;
	
	
	private String currentRead2Basecalls;
	private QualitySequence currentRead2Qualities;
	private String encodedRead2Qualities;
	
	private boolean turnOffCompression;
	
	public AbstractPairedFastqRecordVisitor(String id, String optionalComment,
			FastqQualityCodec qualityCodec){
		this(id,optionalComment, qualityCodec, false);
	}
	public AbstractPairedFastqRecordVisitor(String id, String optionalComment,
			FastqQualityCodec qualityCodec, boolean turnOffCompression) {
		this.id = id;
		this.optionalComment = optionalComment;
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
	    	if(optionalComment ==null){
	    		fastqRecord = new ParsedFastqRecord(id, currentBasecalls , encodedQualities, qualityCodec, turnOffCompression);
	        }else{
	            fastqRecord = new CommentedParsedFastqRecord(id, currentBasecalls , encodedQualities, qualityCodec, turnOffCompression, optionalComment);
	        }       	
	    }else{
	    	 fastqRecord = FastqRecordBuilder.create(id, new NucleotideSequenceBuilder(currentBasecalls)
															.turnOffDataCompression(turnOffCompression)
															.build(), 
													currentQualities)
											.comment(optionalComment)
											.build();
	    }
	    
	    FastqRecord fastq2Record;
	    
	    if(currentRead2Qualities ==null){
	    	if(optionalComment ==null){
	    		fastq2Record = new ParsedFastqRecord(id, currentRead2Basecalls , encodedRead2Qualities, qualityCodec, turnOffCompression);
	        }else{
	            fastq2Record = new CommentedParsedFastqRecord(id, currentRead2Basecalls , encodedRead2Qualities, qualityCodec, turnOffCompression, optionalComment);
	        }       	
	    }else{
	    	 fastq2Record = FastqRecordBuilder.create(id, new NucleotideSequenceBuilder(currentRead2Basecalls)
															.turnOffDataCompression(turnOffCompression)
															.build(), 
													currentRead2Qualities)
											.comment(optionalComment)
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
