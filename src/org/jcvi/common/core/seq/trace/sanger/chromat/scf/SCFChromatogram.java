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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.scf;

import org.jcvi.common.core.seq.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.symbol.qual.QualitySequence;
/**
 * <code>SCFChromatogram</code> is a SCF specific implementation
 * of {@link Chromatogram}.  SCF Chromatograms have additional
 * data such as Substitution, Insertion, and Deletion confidence at each
 * base call.  There is also the possibility of additional "private data".
 * @author dkatzel
 *
 *
 */
public interface SCFChromatogram extends Chromatogram{


    /**
     * {@link SCFChromatogram}s may have additional PrivateData.
     * @return the privateData; or <code>null</code> if there
     * is no {@link PrivateData}.
     */
    PrivateData getPrivateData();
    /**
     * This is a {@link QualitySequence} describing
     * the confidence level that each basecall is 
     * a substitution.  This sequence is optional
     * in the SCF spec.  If this data does 
     * not exist for the given chromatogram,
     * then this value will be null.
     * @return a {@link QualitySequence}; or
     * null if no data exists. 
     */
    QualitySequence getSubstitutionConfidence();
    /**
     * This is a {@link QualitySequence} describing
     * the confidence level that each basecall is 
     * an insertion.  This sequence is optional
     * in the SCF spec.  If this data does 
     * not exist for the given chromatogram,
     * then this value will be null.
     * @return a {@link QualitySequence}; or
     * null if no data exists. 
     */
    QualitySequence getInsertionConfidence();
    /**
     * This is a {@link QualitySequence} describing
     * the confidence level that each basecall is 
     * a deletion.  This sequence is optional
     * in the SCF spec.  If this data does 
     * not exist for the given chromatogram,
     * then this value will be null.
     * @return a {@link QualitySequence}; or
     * null if no data exists. 
     */
    QualitySequence getDeletionConfidence();

}
