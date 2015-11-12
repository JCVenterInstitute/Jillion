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
import java.nio.IntBuffer;

import org.jcvi.jillion.trace.chromat.abi.tag.IntArrayTaggedDataRecord;
import org.jcvi.jillion.trace.chromat.abi.tag.TaggedDataName;
import org.jcvi.jillion.trace.chromat.abi.tag.TaggedDataType;

public class DefaultIntegerArrayTaggedDataRecord  extends AbstractTaggedDataRecord<IntArrayTaggedDataRecord,int[]> implements IntArrayTaggedDataRecord{

	public DefaultIntegerArrayTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected int[] parseDataFrom(byte[] data) {		
		//have to manually build
		ByteBuffer buffer= ByteBuffer.wrap(data);
		IntBuffer result = IntBuffer.allocate(data.length/4);
		while(buffer.hasRemaining()){
			result.put(buffer.getInt());
		}
		return result.array();
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<int[]> getParsedDataType() {
        return int[].class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<IntArrayTaggedDataRecord> getType() {
        return IntArrayTaggedDataRecord.class;
    }


}
