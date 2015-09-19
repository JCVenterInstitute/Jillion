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


public class DefaultTimeTaggedDataRecord extends AbstractTaggedDataRecord<TimeTaggedDataRecord,Ab1LocalTime> implements TimeTaggedDataRecord{

	public DefaultTimeTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected Ab1LocalTime parseDataFrom(byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		
		return new Ab1LocalTime(buf.get(), buf.get(), buf.get());
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<Ab1LocalTime> getParsedDataType() {
        return Ab1LocalTime.class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<TimeTaggedDataRecord> getType() {
        return TimeTaggedDataRecord.class;
    }


}
