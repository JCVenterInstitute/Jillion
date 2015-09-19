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
