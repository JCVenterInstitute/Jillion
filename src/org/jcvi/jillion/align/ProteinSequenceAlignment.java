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
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.internal.align.ProteinSequenceAlignmentBuilder;
/**
 * {@code ProteinSequenceAlignment} is a marker interface for a
 * {@link SequenceAlignment} of {@link AminoAcid}s.
 * @author dkatzel
 *
 */
public interface ProteinSequenceAlignment extends SequenceAlignment<AminoAcid, ProteinSequence>{

	static ProteinSequenceAlignment create(ProteinSequence query, ProteinSequence subject) {
		return create(query, null, subject, null);
	}
	static ProteinSequenceAlignmentBuilder createBuilder(ProteinSequence query, ProteinSequence subject) {
		return createBuilder(query, null, subject, null);
	}
	static ProteinSequenceAlignment create(ProteinSequence query, Range queryRange, ProteinSequence subject, Range subjectRange) {
		return createBuilder(query, null, subject, null).build();
	}
	static ProteinSequenceAlignmentBuilder createBuilder(ProteinSequence query, Range queryRange, ProteinSequence subject, Range subjectRange) {
		if(query.getLength() != subject.getLength()) {
			throw new IllegalArgumentException("query and subject lengths must match");
		}
		
		ProteinSequenceAlignmentBuilder builder = new ProteinSequenceAlignmentBuilder();
		Iterator<AminoAcid> qIter = queryRange ==null? query.iterator() : query.iterator(queryRange);
		Iterator<AminoAcid> sIter = subjectRange==null? subject.iterator() : subject.iterator(subjectRange);
		while(qIter.hasNext()) {
			AminoAcid q = qIter.next();
			AminoAcid s = sIter.next();
			
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
		
		return builder;
	}
}
