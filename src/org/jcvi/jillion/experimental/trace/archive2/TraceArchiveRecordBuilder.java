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
package org.jcvi.jillion.experimental.trace.archive2;

import java.util.Map;

import org.jcvi.jillion.core.util.Builder;


public interface TraceArchiveRecordBuilder extends Builder<TraceArchiveRecord>{

	/**
	 * Puts an attribute with the given key and value.  If 
	 * an attribute already exists with the given
	 * key, it will be overwritten with the new value.
	 * @param traceInfoField the {@link TraceInfoField} to add.
	 * @param value the value associated with the given key.
	 * @return {@code this}
	 */
	TraceArchiveRecordBuilder put(TraceInfoField traceInfoField, String value);

	TraceArchiveRecordBuilder putExtendedData(String key, String value);

	TraceArchiveRecordBuilder putAll(Map<TraceInfoField, String> map);
	@Override
	TraceArchiveRecord build();

}
