/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.chromat.scf.section;

import java.nio.ByteBuffer;

import org.jcvi.jillion.internal.trace.sanger.chromat.scf.section.AbstractSampleSectionCodec;
import org.jcvi.jillion.internal.trace.sanger.chromat.scf.section.Version2SampleSectionCodec;



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
