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

public abstract class AbstractFastqRecordVisitor implements FastqRecordVisitor{

	private final String id;
	private final String optionalComment;
	private final FastqQualityCodec qualityCodec;
	
	private String currentBasecalls;
	private QualitySequence currentQualities;
	private String encodedQualities;
	private boolean turnOffCompression;
	
	public AbstractFastqRecordVisitor(String id, String optionalComment,
			FastqQualityCodec qualityCodec){
		this(id,optionalComment, qualityCodec, false);
	}
	public AbstractFastqRecordVisitor(String id, String optionalComment,
			FastqQualityCodec qualityCodec, boolean turnOffCompression) {
		this.id = id;
		this.optionalComment = optionalComment;
		this.qualityCodec = qualityCodec;
		this.turnOffCompression = turnOffCompression;
	}

	@Override
	public final void visitNucleotides(String nucleotides) {
		currentBasecalls = nucleotides;
		
	}

	@Override
	public final void visitEncodedQualities(String encodedQualities) {
		this.encodedQualities = encodedQualities;
		
	}
	
	

	@Override
	public void visitQualities(QualitySequence qualities) {
		currentQualities = qualities;
		
	}

	@Override
	public final void visitEnd() {
	    FastqRecord fastqRecord;
	    if(currentQualities !=null){
	        fastqRecord = new FastqRecordBuilder(id, new NucleotideSequenceBuilder(currentBasecalls)
	        												.turnOffDataCompression(turnOffCompression)
	        												.build(), currentQualities)
            							.comment(optionalComment)
            							.build();
       	
	    }else{
	        if(optionalComment ==null){
	            fastqRecord = new ParsedFastqRecord(id, currentBasecalls , encodedQualities, qualityCodec, turnOffCompression);
	        }else{
	            fastqRecord = new CommentedParsedFastqRecord(id, currentBasecalls , encodedQualities, qualityCodec, turnOffCompression, optionalComment);
	        }
	    }
	    
	    visitRecord(fastqRecord);
	}
	
	@Override
	public void halted() {
		//no-op			
	}

	protected abstract void visitRecord(FastqRecord record);
	
	

}
