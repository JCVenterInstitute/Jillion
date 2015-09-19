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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSangerFastQQualityCodecActual {

     FastqQualityCodec sut = FastqQualityCodec.SANGER;
    String encodedqualities = "I9IG9IC";
    byte[] qualities = 
            new byte[]{40,24,40,38,24,40,34};
    QualitySequence qualitySequence = new QualitySequenceBuilder(qualities).build();
	
    @Test
    public void decode(){       
        assertEquals(qualitySequence, sut.decode(encodedqualities));
    }
    @Test
    public void encode(){       
        assertEquals(encodedqualities, sut.encode(
        		 qualitySequence));
    }
}
