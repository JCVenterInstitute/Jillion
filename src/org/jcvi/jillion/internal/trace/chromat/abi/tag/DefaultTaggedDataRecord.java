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

public class DefaultTaggedDataRecord extends AbstractTaggedDataRecord<ByteArrayTaggedDataRecord,byte[]> implements ByteArrayTaggedDataRecord{

	public DefaultTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	/* (non-Javadoc)
	 * @see org.jcvi.trace.sanger.chromatogram.ab1.tag.AbstractTaggedDataRecord#parseDataFrom(byte[])
	 */
	@Override
	protected byte[] parseDataFrom(byte[] data) {

		return data;
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<byte[]> getParsedDataType() {
        return byte[].class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<ByteArrayTaggedDataRecord> getType() {
        return ByteArrayTaggedDataRecord.class;
    }

	

}
