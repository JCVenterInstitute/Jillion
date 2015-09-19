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
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import java.nio.ByteBuffer;

import org.jcvi.jillion.internal.trace.chromat.scf.SCFUtils;
import org.jcvi.jillion.internal.trace.chromat.scf.section.AbstractSampleSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Version3SampleSectionCodec;

public class TestVersion3SamplesSection  extends AbstractTestSamplesSection{
    @Override
    protected AbstractSampleSectionCodec createSectionHandler() {
        return new Version3SampleSectionCodec();
    }

    @Override
    protected byte[] encodeBytePositions() {
        ByteBuffer result = ByteBuffer.allocate(aSamplesAsBytes.length*4);
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(aSamplesAsBytes));
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(cSamples));
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(gSamples));
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(tSamples));
        return result.array();
    }

    @Override
    protected byte[] encodeShortPositions() {
        ByteBuffer result = ByteBuffer.allocate(aSamplesAsShorts.length*4*2);
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(aSamplesAsShorts));
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(cSamples));
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(gSamples));
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(tSamples));

        return result.array();
    }



    private void bulkShortPut(ByteBuffer buffer, short[] array){
        for(int i=0; i<array.length; i++){
            buffer.putShort(array[i]);
        }
    }

    private void bulkBytePut(ByteBuffer buffer, short[] arrayOfBytes){
        for(int i=0; i<arrayOfBytes.length; i++){
            buffer.put((byte)arrayOfBytes[i]);
        }
    }
}
