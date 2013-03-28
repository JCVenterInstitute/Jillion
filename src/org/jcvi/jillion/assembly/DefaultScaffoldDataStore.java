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
package org.jcvi.jillion.assembly;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.MapUtil;

public final class DefaultScaffoldDataStore {

	private DefaultScaffoldDataStore(){
		//private constructor.
	}
	public static ScaffoldDataStoreBuilder createBuilder(){
		return new DefaultScaffoldDataStoreBuilder();
	}
	
	private static final class DefaultScaffoldDataStoreBuilder implements ScaffoldDataStoreBuilder{
		private final Map<String, ScaffoldBuilder> builders = new HashMap<String, ScaffoldBuilder>();
		
		@Override
		public synchronized ScaffoldDataStore build() {
			int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(builders.size());
			Map<String, Scaffold> scaffolds = new HashMap<String, Scaffold>(mapSize);
			for(Entry<String, ScaffoldBuilder> entry : builders.entrySet()){
				scaffolds.put(entry.getKey(), entry.getValue().build());
			}
			return DataStoreUtil.adapt(ScaffoldDataStore.class, scaffolds);
		}

		@Override
		public synchronized ScaffoldDataStoreBuilder addScaffold(Scaffold scaffold) {
			String scaffoldId = scaffold.getId();
			if(!builders.containsKey(scaffoldId)){
				builders.put(scaffoldId, DefaultScaffold.createBuilder(scaffoldId));
			}
			for(PlacedContig placedContig: scaffold.getPlacedContigs()){
				builders.get(scaffoldId).add(placedContig);
			}
			return this;
		}

		@Override
		public synchronized ScaffoldDataStoreBuilder addPlacedContig(String scaffoldId,
				String contigId, DirectedRange directedRange) {
			if(!builders.containsKey(scaffoldId)){
				builders.put(scaffoldId, DefaultScaffold.createBuilder(scaffoldId));
			}
			builders.get(scaffoldId).add(contigId, directedRange.asRange(), directedRange.getDirection());
			return this;
		}
		
	}
}
