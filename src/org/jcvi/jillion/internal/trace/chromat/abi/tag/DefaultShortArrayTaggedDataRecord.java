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
package org.jcvi.jillion.internal.trace.chromat.abi.tag;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.jillion.trace.chromat.abi.tag.ShortArrayTaggedDataRecord;
import org.jcvi.jillion.trace.chromat.abi.tag.TaggedDataName;
import org.jcvi.jillion.trace.chromat.abi.tag.TaggedDataType;

public class DefaultShortArrayTaggedDataRecord extends AbstractTaggedDataRecord<ShortArrayTaggedDataRecord,short[]> implements ShortArrayTaggedDataRecord{

	public DefaultShortArrayTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected short[] parseDataFrom(byte[] data) {
		//have to manually build short array 
		ByteBuffer buffer= ByteBuffer.wrap(data);
		ShortBuffer result = ShortBuffer.allocate(data.length/2);
		while(buffer.hasRemaining()){
			result.put(buffer.getShort());
		}
		return result.array();
		
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<short[]> getParsedDataType() {
        return short[].class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<ShortArrayTaggedDataRecord> getType() {
        return ShortArrayTaggedDataRecord.class;
    }

}
