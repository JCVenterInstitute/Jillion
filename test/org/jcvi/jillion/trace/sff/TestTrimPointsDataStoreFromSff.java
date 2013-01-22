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

import org.jcvi.jillion.assembly.util.trim.TrimPointsDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.trace.sff.SffUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestTrimPointsDataStoreFromSff extends AbstractTestExampleSffFile{

	private final TrimPointsDataStore datastore;
	public TestTrimPointsDataStoreFromSff() throws IOException{
		datastore = SffUtil.createTrimPointsDataStoreFrom(SFF_FILE);
	}
	@Test
	public void isATrimPointsDataStore(){
		assertTrue(datastore instanceof TrimPointsDataStore);
	}
	@Test
	public void correctNumberOfRecords() throws DataStoreException{
		assertEquals(5, datastore.getNumberOfRecords());
	}
	@Test
	public void trimDataStoreIsCorrect() throws DataStoreException{		
		
		assertEquals(FF585OX02HCMO2.getQualityClip(),datastore.get("FF585OX02HCMO2"));
		assertEquals(FF585OX02HCD8G.getQualityClip(),datastore.get("FF585OX02HCD8G"));
		assertEquals(FF585OX02FNE4N.getQualityClip(),datastore.get("FF585OX02FNE4N"));
		assertEquals(FF585OX02GMGGN.getQualityClip(),datastore.get("FF585OX02GMGGN"));
		assertEquals(FF585OX02FHO5X.getQualityClip(),datastore.get("FF585OX02FHO5X"));
	}
}
