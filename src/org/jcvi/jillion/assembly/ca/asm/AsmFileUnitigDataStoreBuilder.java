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
package org.jcvi.jillion.assembly.ca.asm;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ca.frg.Fragment;
import org.jcvi.jillion.assembly.ca.frg.FragmentDataStore;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.Builder;

public class AsmFileUnitigDataStoreBuilder implements Builder<AsmUnitigDataStore>{

	private final File asmFile;
	
	private DataStoreProviderHint hint = DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED;
	
	private DataStoreFilter filter = DataStoreFilters.alwaysAccept();
	
	
	private final  DataStore<NucleotideSequence> fullLengthSequences;
	
	
	public AsmFileUnitigDataStoreBuilder(File asmFile,
			NucleotideSequenceDataStore fullLengthSequences) {
		if(asmFile ==null){
			throw new NullPointerException("asm file can not be null");
		}
		if(fullLengthSequences ==null){
			throw new NullPointerException("fullLengthSequence DataStore can not be null");
		}
		this.asmFile = asmFile;
		this.fullLengthSequences = fullLengthSequences;
	}

	public AsmFileUnitigDataStoreBuilder(File asmFile,
			FragmentDataStore frgDataStore) {
		if(asmFile ==null){
			throw new NullPointerException("asm file can not be null");
		}
		if(frgDataStore ==null){
			throw new NullPointerException("frgDataStore can not be null");
		}
		this.asmFile = asmFile;
		this.fullLengthSequences = adaptFrgDataStore(frgDataStore);
	}

	
	public AsmFileUnitigDataStoreBuilder filter(DataStoreFilter filter){
		if(filter ==null){
			throw new NullPointerException("filter can not be null");
		}
		this.filter =filter;
		return this;
	}
	
	
	public AsmFileUnitigDataStoreBuilder hint(DataStoreProviderHint hint){
		if(hint ==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint =hint;
		return this;
	}
	
	private static NucleotideSequenceDataStore adaptFrgDataStore(
			FragmentDataStore frgDataStore) {
		return DataStoreUtil.adapt(NucleotideSequenceDataStore.class, frgDataStore, 
				new DataStoreUtil.AdapterCallback<Fragment, NucleotideSequence>() {

			@Override
			public NucleotideSequence get(Fragment from) {
				return from.getNucleotideSequence();
			}
	
		});
	}

	@Override
	public AsmUnitigDataStore build() {
		try {
		switch(hint){
			case RANDOM_ACCESS_OPTIMIZE_SPEED:				
					return DefaultAsmUnitigDataStore.create(asmFile, fullLengthSequences, filter);
			case RANDOM_ACCESS_OPTIMIZE_MEMORY:
					return IndexedAsmFileUnitigDataStore.create(asmFile, fullLengthSequences, filter);
			case ITERATION_ONLY:
				//no large implementation so return indexed?
				//TODO add large asm implementation
				return IndexedAsmFileUnitigDataStore.create(asmFile, fullLengthSequences, filter);
			default:
				throw new IllegalArgumentException("unknown hint "+ hint);
			}
		} catch (IOException e) {
			throw new IllegalStateException("error parsing asm file", e);
		}
	}

}
