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
package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.util.Builder;
/**
 * {@code FastaDataStoreBuilder} is a {@link Builder}
 * for {@link DataStore}s of {@link FastaRecord}s.
 * @author dkatzel
 *
 * @param <S> the type of object in the sequence of the fasta.
 * @param <T> the {@link Sequence} of the fasta.
 * @param <F> the {@link FastaRecord} type.
 * @param <D> the {@link DataStore} type to build.
 */
public interface FastaDataStoreBuilder<S, T extends Sequence<S>, F extends FastaRecord<S, T>, D extends DataStore<F>> extends Builder<D>{
	/**
	 * Add the given {@link FastaRecord} to this builder.
	 * If a FastaRecord with the same id already exists in this builder
	 * then that record gets overwritten by this new record
	 * (similar to adding an entry to a map with the same key).
	 * @param fastaRecord the fastaRecord to add.
	 * @return this.
	 * @throws NullPointerException if fastaRecord is null.
	 */
	<E extends F> FastaDataStoreBuilder<S,T,F,D> addFastaRecord(E fastaRecord);
	
}
