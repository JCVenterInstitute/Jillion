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

import org.jcvi.jillion.trace.chromat.abi.tag.Ab1LocalDate;
import org.jcvi.jillion.trace.chromat.abi.tag.DateTaggedDataRecord;
import org.jcvi.jillion.trace.chromat.abi.tag.TaggedDataName;
import org.jcvi.jillion.trace.chromat.abi.tag.TaggedDataType;

public class DefaultDateTaggedDataRecord extends AbstractTaggedDataRecord<DateTaggedDataRecord,Ab1LocalDate> implements DateTaggedDataRecord{

	public DefaultDateTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected Ab1LocalDate parseDataFrom(byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		short year =buf.getShort();
		int month = buf.get()-1;
		byte day = buf.get();
		
		return new Ab1LocalDate(year, month, day);
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<Ab1LocalDate> getParsedDataType() {
        return Ab1LocalDate.class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<DateTaggedDataRecord> getType() {
        return DateTaggedDataRecord.class;
    }



}
