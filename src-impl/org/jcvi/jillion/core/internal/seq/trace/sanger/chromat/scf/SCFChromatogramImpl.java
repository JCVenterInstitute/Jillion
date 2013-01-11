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
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf;

import org.jcvi.common.core.seq.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.PrivateData;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.ScfChromatogram;
import org.jcvi.common.core.util.ObjectsUtil;
import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.BasicChromatogram;
import org.jcvi.jillion.core.qual.QualitySequence;

public class SCFChromatogramImpl extends BasicChromatogram implements ScfChromatogram {

    private PrivateData privateData;

    private QualitySequence substitutionConfidence;
    private QualitySequence insertionConfidence;
    private QualitySequence deletionConfidence;

    public SCFChromatogramImpl(Chromatogram c){
        this(c,null,null,null,null);
    }
    public SCFChromatogramImpl(Chromatogram c,
    		QualitySequence subtitutionConfidence,
    		QualitySequence insertionConfidence,
    		QualitySequence deletConfidence,
            PrivateData privateData
            ) {
        super(c);
        this.substitutionConfidence = subtitutionConfidence;
        this.deletionConfidence = deletConfidence;
        this.insertionConfidence = insertionConfidence;
        this.privateData = privateData;
    }


    /**
     * @return the privateData
     */
    @Override
    public PrivateData getPrivateData() {
        return privateData;
    }

    /**
     * @return the substitutionConfidence
     */
    @Override
    public QualitySequence getSubstitutionConfidence() {
        return substitutionConfidence;
    }

    /**
     * @return the insertionConfidence
     */
    @Override
    public QualitySequence getInsertionConfidence() {
        return insertionConfidence;
    }

    /**
     * @return the deletionConfidence
     */
    @Override
    public QualitySequence getDeletionConfidence() {
        return deletionConfidence;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ObjectsUtil.nullSafeHashCode(deletionConfidence);
        result = prime * result + ObjectsUtil.nullSafeHashCode(insertionConfidence);
        result = prime * result + ObjectsUtil.nullSafeHashCode(privateData);
        result = prime * result+ ObjectsUtil.nullSafeHashCode(substitutionConfidence);
        return result;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!super.equals(obj)){
            return false;
        }
        if (!(obj instanceof ScfChromatogram)){
            return false;
        }
        final ScfChromatogram other = (ScfChromatogram) obj;
        return ObjectsUtil.nullSafeEquals(getDeletionConfidence(), other.getDeletionConfidence())
            && ObjectsUtil.nullSafeEquals(getInsertionConfidence(), other.getInsertionConfidence())
            && ObjectsUtil.nullSafeEquals(getSubstitutionConfidence(), other.getSubstitutionConfidence())
             && ObjectsUtil.nullSafeEquals(getPrivateData(), other.getPrivateData());


    }


}
