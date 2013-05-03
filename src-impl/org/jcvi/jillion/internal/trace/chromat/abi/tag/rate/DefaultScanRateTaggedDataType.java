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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.trace.chromat.abi.tag.rate;

import java.nio.ByteBuffer;

import org.jcvi.jillion.internal.trace.chromat.abi.tag.AbstractTaggedDataRecord;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.TaggedDataName;
import org.jcvi.jillion.internal.trace.chromat.abi.tag.TaggedDataType;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultScanRateTaggedDataType extends AbstractTaggedDataRecord<ScanRateTaggedDataType, ScanRate> implements ScanRateTaggedDataType{

 
    public DefaultScanRateTaggedDataType(TaggedDataName name, long number,
            TaggedDataType dataType, int elementLength, long numberOfElements,
            long recordLength, long dataRecord, long crypticValue) {
        super(name, number, dataType, elementLength, numberOfElements, recordLength,
                dataRecord, crypticValue);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<ScanRate> getParsedDataType() {
        return ScanRate.class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<ScanRateTaggedDataType> getType() {
        return ScanRateTaggedDataType.class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected ScanRate parseDataFrom(byte[] data) {
      ByteBuffer buf = ByteBuffer.wrap(data);
       DefaultScanRate.Builder builder = new DefaultScanRate.Builder();
       builder.time(buf.getInt())
               .period(buf.getInt())
               .firstScanLine(buf.getInt());
       
        return builder.build();
    }

    

}
