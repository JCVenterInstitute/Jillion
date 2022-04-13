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
package org.jcvi.jillion.align.pairwise;

import java.util.Iterator;

import org.jcvi.jillion.align.SequenceAlignment;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
/**
 * {@code PairwiseSequenceAlignment} is a {@link SequenceAlignment}
 * between two {@link org.jcvi.jillion.core.Sequence}s.
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue} in the sequence.
 * @param <S> the type of {@link org.jcvi.jillion.core.Sequence} in this alignment.
 */
public interface PairwiseSequenceAlignment<R extends Residue, S extends ResidueSequence<R, S, ?>> extends SequenceAlignment<R, S> {
	/**
	 * Get the score of this alignment that 
	 * was computed from the {@link org.jcvi.jillion.align.SubstitutionMatrix}
	 * used to make the alignment.
	 * @return the score as a float depending
	 * on the type of alignment and values
	 * of the scoring matrix this could negative.
	 */
	float getScore();
	
	default String toFormattedString() {
		return toFormattedString(80);
	}
	default String toFormattedString(int residuesPerLine) {
		if(residuesPerLine < 1) {
			throw new IllegalArgumentException("must have at least 1 residue per line");
		}
		S gappedQuery = getGappedQueryAlignment();
        S gappedSubject = getGappedSubjectAlignment();

        int qOffset = (int) getQueryRange().getBegin();
        int tOffset = (int) getSubjectRange().getBegin();
        Iterator<R> qIter = gappedQuery.iterator();
        Iterator<R> sIter = gappedSubject.iterator();
        int qGaps = 0, sGaps = 0;
        int currentOffset = 0;
        StringBuilder fullBuilder = new StringBuilder(4 * getAlignmentLength());

        StringBuilder topBuilder = new StringBuilder(100);
        StringBuilder middleBuilder = new StringBuilder(100);
        StringBuilder bottomBuilder = new StringBuilder(100);


        int count=0;
        boolean hasDataOnCurrentLine=false;
        if(qIter.hasNext()){
            topBuilder.append(   String.format("Query  %5d  ",qOffset + currentOffset +1));
            middleBuilder.append("              ");
            bottomBuilder.append(String.format("Sbjct  %5d  ",tOffset + currentOffset +1));

        }
        while (qIter.hasNext()) {
            hasDataOnCurrentLine = true;
            Residue q = qIter.next();
            Residue s = sIter.next();
            if (q.isGap()) {
                qGaps++;
                
                middleBuilder.append(' ');
            } else if (s.isGap()) {
                sGaps++;
                middleBuilder.append(' ');
            } else if (q.equals(s)) {

                middleBuilder.append('|');
              
            } else {
                middleBuilder.append(' ');
            }
            topBuilder.append(q.getCharacter());
            bottomBuilder.append(s.getCharacter());
            count++;
            if(count % residuesPerLine ==0){
                topBuilder.append(   String.format("  %5d  %n",qOffset + currentOffset - qGaps +1));
                middleBuilder.append(System.lineSeparator());
                bottomBuilder.append(String.format("  %5d  %n",tOffset + currentOffset - sGaps +1));

                fullBuilder.append(topBuilder).append(middleBuilder).append(bottomBuilder);
                topBuilder.setLength(0);
                middleBuilder.setLength(0);
                bottomBuilder.setLength(0);
                //check for next line
                if(qIter.hasNext()){
                    //more data
                    topBuilder.append(   String.format("Query  %5d  ",qOffset + currentOffset -qGaps +2));
                    middleBuilder.append("              ");
                    bottomBuilder.append(String.format("Sbjct  %5d  ",tOffset + currentOffset -sGaps +2));
                }else{
                    hasDataOnCurrentLine = false;
                }
            }
            currentOffset++;

        }
        if(hasDataOnCurrentLine){
              //end of last line of something
            //current offset is already at +1 so we don't need a +1 for the end of the line
            topBuilder.append(   String.format("  %5d  %n",qOffset + currentOffset -qGaps));
            middleBuilder.append(System.lineSeparator());
            bottomBuilder.append(String.format("  %5d  %n",tOffset + currentOffset -sGaps));
            fullBuilder.append(topBuilder).append(middleBuilder).append(bottomBuilder);
        }
        return fullBuilder.toString();
	}
}
