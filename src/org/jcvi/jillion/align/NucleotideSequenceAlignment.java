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
package org.jcvi.jillion.align;

import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.align.NucleotideSequenceAlignmentBuilder;

public interface NucleotideSequenceAlignment extends SequenceAlignment<Nucleotide,NucleotideSequence>{

	
	static NucleotideSequenceAlignment create(NucleotideSequence query, NucleotideSequence subject) {
		return create(query, null, subject, null);
	}
	static NucleotideSequenceAlignment create(NucleotideSequence query, Range queryRange, NucleotideSequence subject, Range subjectRange) {
		
		NucleotideSequence querySeq = queryRange==null? query: query.trim(queryRange);
		NucleotideSequence subjectSeq = subjectRange==null? subject: subject.trim(subjectRange);
		
		if(querySeq.getLength() != subjectSeq.getLength()) {
			throw new IllegalArgumentException("query and subject lengths must match");
		}
		
		NucleotideSequenceAlignmentBuilder builder = new NucleotideSequenceAlignmentBuilder();
		Iterator<Nucleotide> qIter = querySeq.iterator();
		Iterator<Nucleotide> sIter = subjectSeq.iterator();
		while(qIter.hasNext()) {
			Nucleotide q = qIter.next();
			Nucleotide s = sIter.next();
			
			if(q.isGap() || s.isGap()) {
				builder.addGap(q, s);
			}else if(q ==s) {
				builder.addMatch(q);
			}else {
				builder.addMismatch(q, s);
			}
		}
		
		builder.setAlignmentOffsets(queryRange ==null? 0: (int) queryRange.getBegin(), 
									subjectRange==null? 0 : (int) subjectRange.getBegin());
		
		return builder.build();
	}
}
