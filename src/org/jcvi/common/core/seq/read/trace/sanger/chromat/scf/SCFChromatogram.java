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
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Confidence;
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
    Confidence getSubstitutionConfidence();
    Confidence getInsertionConfidence();
    Confidence getDeletionConfidence();

}
