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
/*
 * Created on Dec 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * an {@code AceContig} is a {@link Contig}
 * from an ace file.  Each {@link AceContig}
 * is made up of {@link AceAssembledRead}s.
 * @author dkatzel
 */
public interface AceContig extends Contig<AceAssembledRead>{
    /**
     * Is this contig complemented?
     * @return {@code true} if this contig
     * is complemented; {@code false} otherwise.
     */
    boolean isComplemented();
    /**
     * Get the {@link QualitySequence}
     * for the UNGAPPED consensus sequence.
     * @return a {@link QualitySequence},
     * may be null if the consensus qualities are unavailable.
     */
    QualitySequence getConsensusQualitySequence();
    
    /**
     * The order of the reads returned by this
     * iterator is ordered by {@link AceAssembledRead#getGappedStartOffset()}.
     * If two reads have the same start offset, then the shorter read determined
     * by {@link AceAssembledRead#getGappedLength()} is returned.
     * If two reads have the same start offset AND gapped length,
     * the the read with the lexigraphically lower read id is returned.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    StreamingIterator<AceAssembledRead> getReadIterator();
    
}
