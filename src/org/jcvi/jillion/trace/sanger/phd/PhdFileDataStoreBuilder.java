package org.jcvi.jillion.trace.sanger.phd;

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
	
	private DataStoreProviderHint hint = DataStoreProviderHint.OPTIMIZE_FAST_RANDOM_ACCESS;
	
	
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
				return DefaultPhdDataStore2.create(inputStream, filter);
			}
			
			switch(hint){
				case OPTIMIZE_FAST_RANDOM_ACCESS : 
					return DefaultPhdDataStore2.create(phdFile, filter);
				case OPTIMIZE_LOW_MEMORY_RANDOM_ACCESS:
					return IndexedPhdDataStore2.create(phdFile, filter);
				case ITERATION_ONLY:
					return new LargePhdballDataStore2(phdFile, filter);
				default: throw new IllegalStateException("unknown hint "+ hint);
			}
		}catch(IOException e){
			throw new IllegalStateException("error bulding phd datastore ",e);
		}
	}

}
