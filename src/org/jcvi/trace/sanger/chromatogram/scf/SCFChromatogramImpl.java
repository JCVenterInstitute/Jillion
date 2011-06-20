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
package org.jcvi.trace.sanger.chromatogram.scf;

import org.jcvi.CommonUtil;
import org.jcvi.sequence.Confidence;
import org.jcvi.trace.sanger.chromatogram.BasicChromatogram;
import org.jcvi.trace.sanger.chromatogram.Chromatogram;

public class SCFChromatogramImpl extends BasicChromatogram implements SCFChromatogram {

    private PrivateData privateData;

    private Confidence substitutionConfidence;
    private Confidence insertionConfidence;
    private Confidence deletionConfidence;

    public SCFChromatogramImpl(Chromatogram c){
        this(c,null,null,null,null);
    }
    public SCFChromatogramImpl(Chromatogram c,
            Confidence subtitutionConfidence,
            Confidence insertionConfidence,
            Confidence deletConfidence,
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
    public PrivateData getPrivateData() {
        return privateData;
    }

    /**
     * @return the substitutionConfidence
     */
    public Confidence getSubstitutionConfidence() {
        return substitutionConfidence;
    }

    /**
     * @return the insertionConfidence
     */
    public Confidence getInsertionConfidence() {
        return insertionConfidence;
    }

    /**
     * @return the deletionConfidence
     */
    public Confidence getDeletionConfidence() {
        return deletionConfidence;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + CommonUtil.hashCode(deletionConfidence);
        result = prime * result + CommonUtil.hashCode(insertionConfidence);
        result = prime * result + CommonUtil.hashCode(privateData);
        result = prime * result+ CommonUtil.hashCode(substitutionConfidence);
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
        if (!(obj instanceof SCFChromatogram)){
            return false;
        }
        final SCFChromatogram other = (SCFChromatogram) obj;
        return CommonUtil.similarTo(getDeletionConfidence(), other.getDeletionConfidence())
            && CommonUtil.similarTo(getInsertionConfidence(), other.getInsertionConfidence())
            && CommonUtil.similarTo(getSubstitutionConfidence(), other.getSubstitutionConfidence())
             && CommonUtil.similarTo(getPrivateData(), other.getPrivateData());


    }


}
