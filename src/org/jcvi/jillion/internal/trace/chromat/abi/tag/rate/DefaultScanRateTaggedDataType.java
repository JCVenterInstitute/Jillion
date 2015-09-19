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
