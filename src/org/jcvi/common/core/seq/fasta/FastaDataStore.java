/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fasta;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.symbol.Symbol;
import org.jcvi.common.core.symbol.Sequence;
/**
 * {@code FastaDataStore} is a marker interface
 * for a {@link DataStore} for {@link FastaRecord}s.
 * @author dkatzel
 *
 * @param <S> the type of {@link Symbol} in the fasta encoding.
 * @param <T> the type of {@link Sequence} of {@link Symbol}s in the fasta.
 * @param <F> the type of {@link FastaRecord} in the datastore.
 */
public interface FastaDataStore<S extends Symbol, T extends Sequence<S>,F extends FastaRecord<S,T>> extends DataStore<F>{

    

}
