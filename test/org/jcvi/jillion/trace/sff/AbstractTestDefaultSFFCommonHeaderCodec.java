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
 * Created on Oct 13, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.math.BigInteger;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.sff.DefaultSFFCommonHeaderDecoder;
import org.jcvi.jillion.trace.sff.DefaultSffCommonHeader;
import org.jcvi.jillion.trace.sff.SffUtil;

public class AbstractTestDefaultSFFCommonHeaderCodec {

    protected BigInteger indexOffset=BigInteger.valueOf(100000L);
    protected int indexLength = 2000;
    protected int numberOfReads = 5;
    protected short numberOfFlowsPerRead = 12;
    protected NucleotideSequence flow = new NucleotideSequenceBuilder("TCAGTCAGTCAG").build();
    protected NucleotideSequence keySequence = new NucleotideSequenceBuilder("TCAG").build();
    protected short headerLength = (short)(31+numberOfFlowsPerRead+SffUtil.caclulatePaddedBytes(31+numberOfFlowsPerRead));

    protected DefaultSffCommonHeader expectedHeader = new DefaultSffCommonHeader(indexOffset,  indexLength,
             numberOfReads,  numberOfFlowsPerRead,  flow,
             keySequence);

    protected DefaultSFFCommonHeaderDecoder sut = DefaultSFFCommonHeaderDecoder.INSTANCE;
}
