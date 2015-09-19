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

import org.jcvi.jillion.internal.trace.chromat.scf.section.AbstractSampleSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Version2SampleSectionCodec;



public class TestVersion2SamplesSection extends AbstractTestSamplesSection{

    /**
    * {@inheritDoc}
    */
    @Override
    protected AbstractSampleSectionCodec createSectionHandler() {
        return new Version2SampleSectionCodec();
    }
    @Override
    protected byte[] encodeShortPositions(){
        ByteBuffer result = ByteBuffer.allocate(aSamplesAsShorts.length*4*2);
        for(int i=0; i< aSamplesAsShorts.length; i++){
          result.putShort(aSamplesAsShorts[i]);
          result.putShort(cSamples[i]);
          result.putShort(gSamples[i]);
          result.putShort(tSamples[i]);
        }
        return result.array();
    }
    @Override
    protected byte[] encodeBytePositions(){

        ByteBuffer result = ByteBuffer.allocate(aSamplesAsBytes.length*4);
        for(int i=0; i< aSamplesAsBytes.length; i++){
          result.put((byte)aSamplesAsBytes[i]);
          result.put((byte)cSamples[i]);
          result.put((byte)gSamples[i]);
          result.put((byte)tSamples[i]);
        }
        return result.array();
    }


}
