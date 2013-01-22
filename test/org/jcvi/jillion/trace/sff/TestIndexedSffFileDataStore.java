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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.sff;

import java.io.IOException;

import org.jcvi.jillion.trace.sff.FlowgramDataStore;
import org.jcvi.jillion.trace.sff.Indexed454SffFileDataStore;
import org.jcvi.jillion.trace.sff.IndexedSffFileDataStore;
import org.jcvi.jillion.trace.sff.IndexedSffFileDataStore.FullPassIndexedSffFileDataStore;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestIndexedSffFileDataStore extends AbstractTestExampleSffFile{

	@Test
	public void returnManifestIndexedInstanceIfManifestIsPresent() throws IOException{
		FlowgramDataStore datastore = IndexedSffFileDataStore.create(SFF_FILE);
		assertTrue(datastore instanceof Indexed454SffFileDataStore);
	}
	@Test
	public void returnManifestIndexedInstanceIfManifestWithNoXMLIsPresent() throws IOException{
		FlowgramDataStore datastore = IndexedSffFileDataStore.create(SFF_FILE_NO_XML);
		assertTrue(datastore instanceof Indexed454SffFileDataStore);
	}
	
	@Test
	public void returnFullyParsedIndexedInstanceIfNoIndexIsPresent() throws IOException{
		FlowgramDataStore datastore = IndexedSffFileDataStore.create(SFF_FILE_NO_INDEX);
		assertTrue(datastore instanceof FullPassIndexedSffFileDataStore);
	}
}
