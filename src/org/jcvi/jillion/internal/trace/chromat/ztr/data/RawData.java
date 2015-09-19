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
 * Created on Oct 27, 2006
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data.Data;



/**
 * This is the implementation of the ZTR Raw Data Format.  This data
 * has no encoding.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 */
public enum RawData implements Data {
    /**
     * Singleton instance of RawData.
     */
    INSTANCE;
    /**
     * Since the given data is not encoded, 
     * return the data back as is.
     * @param data the already completely decoded data
     * @return the same reference to the given data.
     */
    @Override
    public byte[] parseData(byte[] data){
        //this is raw data
       return data;
    }
    /**
     * Creates a new array with the first element as {@link DataHeader#RAW}
     * and the rest of the array exactly matches the elements in the given
     * byte array.
     * @param data the raw data to encode.
     * @throws IOException if there is a problem encoding the data.
     */
	@Override
	public byte[] encodeData(byte[] data) throws IOException {
		ByteBuffer encodedData = ByteBuffer.allocate(data.length+1);
		encodedData.put(DataHeader.RAW);
		encodedData.put(data);
		return encodedData.array();
	}
	/**
	 * This returns the same result as {@link #encodeData(byte[])}
	 * the optional parameter is ignored. 
	 */
	@Override
	public byte[] encodeData(byte[] data, byte ignored)
			throws IOException {
		return encodeData(data);
	}

}
