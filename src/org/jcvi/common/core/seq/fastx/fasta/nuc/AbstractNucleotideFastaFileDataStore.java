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
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.nuc;

import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaFileDataStore;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

/**
 * {@code AbstractNucleotideFastaFileDataStore} is an implementation
 * of {@link AbstractFastaFileDataStore} for {@link NucleotideSequenceFastaRecord}s.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractNucleotideFastaFileDataStore extends AbstractFastaFileDataStore<Nucleotide, NucleotideSequence,NucleotideSequenceFastaRecord> implements NucleotideFastaDataStore{

    private final NucleotideFastaRecordFactory fastaRecordFactory;

    /**
     * @param fastaRecordFactory
     */
    public AbstractNucleotideFastaFileDataStore(
            NucleotideFastaRecordFactory fastaRecordFactory) {
        this.fastaRecordFactory = fastaRecordFactory;
    }
    
    public AbstractNucleotideFastaFileDataStore(){
        this(DefaultNucleotideFastaRecordFactory.getInstance());
    }

    protected final NucleotideFastaRecordFactory getFastaRecordFactory() {
        return fastaRecordFactory;
    }

    
    

}
