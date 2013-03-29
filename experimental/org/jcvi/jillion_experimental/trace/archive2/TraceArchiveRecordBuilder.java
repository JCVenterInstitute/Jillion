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
package org.jcvi.jillion_experimental.trace.archive2;

import java.util.Map;

import org.jcvi.jillion.core.util.Builder;


public interface TraceArchiveRecordBuilder extends Builder<TraceArchiveRecord>{

	/**
	 * Puts an attribute with the given key and value.  If 
	 * an attribute already exists with the given
	 * key, it will be overwritten with the new value.
	 * @param key the key to add.
	 * @param value the value associated with the given key.
	 * @return {@code this}
	 */
	TraceArchiveRecordBuilder put(TraceInfoField traceInfoField, String value);

	TraceArchiveRecordBuilder putExtendedData(String key, String value);

	TraceArchiveRecordBuilder putAll(Map<TraceInfoField, String> map);
	@Override
	TraceArchiveRecord build();

}
