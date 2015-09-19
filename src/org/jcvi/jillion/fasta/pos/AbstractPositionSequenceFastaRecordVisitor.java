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
package org.jcvi.jillion.fasta.pos;

import java.util.Scanner;

import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
/**
 * {@code AbstractPositionSequenceFastaRecordVisitor} is an abstract
 * implementation of {@link FastaRecordVisitor} that will collect
 * the visit methods <strong>for a single fasta record</strong>
 * and build an instance of {@link PositionSequenceFastaRecord}.
 * When {@link FastaRecordVisitor#visitEnd()} is called,
 * the {@link PositionSequenceFastaRecord} is built
 * and the abstract method {@link #visitRecord(PositionSequenceFastaRecord)}
 * will be called.  
 * 
 * <p/>
 * A new instance of this class should be used for each fasta record
 * to be visited.  This class is not threadsafe.
 * @author dkatzel
 *
 */
public abstract class AbstractPositionSequenceFastaRecordVisitor implements FastaRecordVisitor{
	
	/**
	 * Default capacity for position builder {@value}
	 * should be large enough to handle
	 * most sanger reads, and the builder
	 * will grow to accommodate larger reads.
	 */
	private static final int DEFAULT_INITIAL_CAPACITY = 900;

	
	private final String id;
	private final String comment;
	//multiplied by 4 since we have to account for all the digits plus whitespace
	private final StringBuilder bodyBuilder = new StringBuilder(DEFAULT_INITIAL_CAPACITY*6);
	
	
	
	public AbstractPositionSequenceFastaRecordVisitor(String id, String comment) {
		this.id = id;
		this.comment = comment;
	}

	@Override
	public final void visitBodyLine(String line) {
		bodyBuilder.append(line);		
	}

	@Override
	public final void visitEnd() {
		PositionSequenceBuilder builder = new PositionSequenceBuilder(DEFAULT_INITIAL_CAPACITY);
		Scanner scanner = new Scanner(bodyBuilder.toString());
        while(scanner.hasNextShort()){
        	builder.append(scanner.nextShort());
        }
		scanner.close();
		PositionFastaRecord record= new PositionFastaRecord(id, comment, builder.build());
		visitRecord(record);		
	}
	@Override
	public void halted() {
		//no-op				
	}
	protected abstract void visitRecord(PositionFastaRecord fastaRecord);
	
}
