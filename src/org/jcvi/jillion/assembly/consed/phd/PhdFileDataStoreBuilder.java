/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
package org.jcvi.jillion.assembly.consed.phd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.Builder;

public final class PhdFileDataStoreBuilder implements Builder<PhdDataStore>{

	private final File phdFile;
	private final InputStream inputStream;
	
	private DataStoreFilter filter = DataStoreFilters.alwaysAccept();
	
	private DataStoreProviderHint hint = DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED;
	
	
	public PhdFileDataStoreBuilder(File phdFile) {
		if(phdFile==null){
			throw new NullPointerException("phd file can not be null");
		}
		if(!phdFile.exists()){
			throw new NullPointerException("phd file does not exist : " + phdFile.getAbsolutePath());
		}
		this.phdFile = phdFile;
		this.inputStream = null;
	}
	
	public PhdFileDataStoreBuilder(InputStream inputStream) {
		if(inputStream==null){
			throw new NullPointerException("inputStream can not be null");
		}
		this.phdFile = null;
		this.inputStream = inputStream;
	}

	public PhdFileDataStoreBuilder filter(DataStoreFilter filter){
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		this.filter = filter;
		return this;
	}

	public PhdFileDataStoreBuilder hint(DataStoreProviderHint hint){
		if(hint==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint = hint;
		return this;
	}

	@Override
	public PhdDataStore build() {
		
		try{
			if(inputStream!=null){
				//need to store everything in memory?
				return DefaultPhdDataStore.create(inputStream, filter);
			}
			
			switch(hint){
				case RANDOM_ACCESS_OPTIMIZE_SPEED : 
					return DefaultPhdDataStore.create(phdFile, filter);
				case RANDOM_ACCESS_OPTIMIZE_MEMORY:
					return IndexedPhdDataStore.create(phdFile, filter);
				case ITERATION_ONLY:
					return new LargePhdballDataStore(phdFile, filter);
				default: throw new IllegalStateException("unknown hint "+ hint);
			}
		}catch(IOException e){
			throw new IllegalStateException("error bulding phd datastore ",e);
		}
	}

}
