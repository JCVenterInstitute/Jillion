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
package org.jcvi.jillion.assembly.clc.cas.var;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcvi.jillion.core.datastore.DataStoreUtil;
/**
 * {@code DefaultReferenceVariationsDataStore} is an 
 * implementation of {@link ReferenceVariationsDataStore}
 * that stores all varition information for all of its 
 * references in memory.  This can be quite memory intensive and is not
 * recommended when many variations are found.
 * @author dkatzel
 *
 */
public final class DefaultReferenceVariationsDataStore {

	private DefaultReferenceVariationsDataStore(){
		//can not instantiate
	}
	/**
	 * Parse the given find_variations log file and 
	 * create a new {@link ReferenceVariationsDataStore} instance containing
	 * all the variations found for all the references in the file. 
	 * @param logFile the find variations file to parse; 
	 * must exist and can not be null.
	 * @return a new {@link ReferenceVariationsDataStore} as described above.
	 * @throws IOException if there is a problem finding or parsing the file.
	 */
	public static ReferenceVariationsDataStore createFromLogFile(File logFile) throws IOException{
		Builder builder = new Builder();
		VariationLogFileParser.parse(logFile, builder);
		return builder.build();
	}
	
	
	private static class Builder implements VariationLogFileVisitor, org.jcvi.jillion.core.util.Builder<ReferenceVariationsDataStore> {

	    private SortedMap<Long, Variation> currentMap=null;
	    private final Map<String, ReferenceVariations> map = new LinkedHashMap<String, ReferenceVariations>();
	    private String currentId;
	    
	    @Override
		public ReferenceVariationsDataStore build() {
			return DataStoreUtil.adapt(ReferenceVariationsDataStore.class,map);
		}

		/**
	    * {@inheritDoc}
	    */
	    @Override
	    public boolean visitReference(String id) {
	        if(currentMap !=null){
	        	map.put(currentId, new DefaultReferenceVariations(currentId, currentMap));
	        }
	        currentMap=new TreeMap<Long, Variation>();
	        currentId =id;
	        return true;
	    }
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public void visitVariation(Variation variation) {
	        long coordinate = variation.getCoordinate();
	        currentMap.put(coordinate, variation);
	        
	    }
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public void visitLine(String line) {
	    	//no-op
	    }
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public void visitEndOfFile() {
	    	if(currentMap !=null){
	    		map.put(currentId, new DefaultReferenceVariations(currentId, currentMap));
	        }
	    }
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public void visitFile() {
	    	//no-op
	    }
	    
	    
	    
	}
}
