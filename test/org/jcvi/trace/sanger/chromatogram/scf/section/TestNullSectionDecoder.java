/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import org.junit.Assert;

import org.jcvi.trace.sanger.chromatogram.scf.section.NullSectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionDecoderException;
import org.junit.Test;

public class TestNullSectionDecoder {

    @Test
    public void parseDoesNothing() throws SectionDecoderException{
        long currentOffset = 123456L;
        Assert.assertEquals("current offset should not change",
                currentOffset,
                new NullSectionCodec().decode(null, currentOffset, null, null));
    }
}
