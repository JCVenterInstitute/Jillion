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
			throw new NullPointerException("phd file must exist");
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
