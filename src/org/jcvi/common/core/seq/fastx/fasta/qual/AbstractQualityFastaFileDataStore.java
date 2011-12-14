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
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.qual;

import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaFileDataStore;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;

/**
 * {@code AbstractQualityFastaFileDataStore} is an implementation
 * of {@link AbstractFastaFileDataStore} for {@code  FastaRecord<EncodedGlyphs<PhredQuality>>}s.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractQualityFastaFileDataStore extends AbstractFastaFileDataStore<PhredQuality, QualitySequence,QualityFastaRecord> implements QualityFastaDataStore{

    private final QualityFastaRecordFactory fastaRecordFactory;

    /**
     * @param fastaRecordFactory
     */
    public AbstractQualityFastaFileDataStore(
            QualityFastaRecordFactory fastaRecordFactory) {
        this.fastaRecordFactory = fastaRecordFactory;
    }
    /**
     * Convenience constructor using the {@link DefaultQualityFastaRecordFactory}.
     * This call is the same as {@link #AbstractQualityFastaFileDataStore(QualityFastaRecordFactory)
     * new AbstractQualityFastaFileDataStore(DefaultQualityFastaRecordFactory.getInstance());}
     */
    public AbstractQualityFastaFileDataStore(){
        this(DefaultQualityFastaRecordFactory.getInstance());
    }
    protected final QualityFastaRecordFactory getFastaRecordFactory() {
        return fastaRecordFactory;
    }

}
