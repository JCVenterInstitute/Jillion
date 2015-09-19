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
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import org.junit.Assert;

import org.jcvi.jillion.internal.trace.chromat.scf.section.NullSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionDecoderException;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramFileVisitor;
import org.junit.Test;

public class TestNullSectionDecoder {

    @Test
    public void parseDoesNothing() throws SectionDecoderException{
        long currentOffset = 123456L;
        Assert.assertEquals("current offset should not change",
                currentOffset,
                new NullSectionCodec().decode(null, currentOffset, null, (ScfChromatogramFileVisitor)null));
    }
}
