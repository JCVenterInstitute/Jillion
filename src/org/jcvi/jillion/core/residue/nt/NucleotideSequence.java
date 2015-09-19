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
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;


import java.io.Serializable;

import org.jcvi.jillion.core.residue.ResidueSequence;
/**
 * {@code NucleotideSequence} an interface to abstract
 * how a {@link Sequence} of {@link Nucleotide}s are encoded in memory.  Nucleotide data
 * can be stored in many different ways depending
 * on the use case and size and composition of the sequence.
 * Different encoding implementations can take up more or less memory or require
 * more computations to decode.  This interface hides implementation details
 * regarding the decoding so users don't have to worry about it.
 * <br/>
 * {@link NucleotideSequence} is {@link Serializable} in a (hopefully)
 * forwards compatible way. However, there is no 
 * guarantee that the implementation will be the same
 * or even that the implementation class will be the same;
 * but the deserialized object should always be equal
 * to the sequence that was serialized.
 * @author dkatzel
 */
public interface NucleotideSequence extends ResidueSequence<Nucleotide>, Serializable{
	/**
     * Two {@link NucleotideSequence}s are equal
     * if they contain the same {@link Nucleotide}s 
     * in the same order.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    boolean equals(Object o);
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    int hashCode();
    
    /**
     * Creates a new {@link NucleotideSequenceBuilder}
     * object. 
     * @return a new {@link NucleotideSequenceBuilder}
     * instance initialized to the this Sequence;
     * will never be null but may be empty.
     * @implSpec
     *  This is the same as
     * <pre>
     * return {@code new NucleotideSequenceBuilder(this)}
     * </pre>
     * @implNote
     * Implementations of this method should add
     * any additional settings or flags to optimize
     * the Builder to that
     * the final built Sequence should be the same
     * class with the same optimization characteristics
     * as this Sequence instance. For example,
     * a {@link ReferenceMappedNucleotideSequence}
     * should make a builder that uses the same reference.
     * @since 5.0
     */
    @Override
    NucleotideSequenceBuilder toBuilder();
}
