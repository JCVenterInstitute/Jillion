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
 * Created on Oct 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import java.util.Arrays;

import org.jcvi.sequence.Confidence;
import org.jcvi.sequence.DefaultConfidence;
import org.jcvi.trace.sanger.chromatogram.BasicChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.Chromatogram;


public class SCFChromatogramBuilder extends BasicChromatogramBuilder{

    
    private byte[] substitutionConfidence;
    private byte[] insertionConfidence;
    private byte[] deletionConfidence;

    private byte[] privateData;
   

    /**
     * @return the substitutionConfidence
     */
    public byte[] substitutionConfidence() {
        return substitutionConfidence==null?null:Arrays.copyOf(substitutionConfidence, substitutionConfidence.length);
    }

    /**
     * @param substitutionConfidence the substitutionConfidence to set
     */
    public SCFChromatogramBuilder substitutionConfidence(byte[] substitutionConfidence) {
        this.substitutionConfidence = substitutionConfidence ==null?null:Arrays.copyOf(substitutionConfidence, substitutionConfidence.length);
        return this;
    }

    /**
     * @return the insertionConfidence
     */
    public byte[] insertionConfidence() {
        return insertionConfidence==null?null:Arrays.copyOf(insertionConfidence, insertionConfidence.length);
    }

    /**
     * @param insertionConfidence the insertionConfidence to set
     */
    public SCFChromatogramBuilder insertionConfidence(byte[] insertionConfidence) {
        this.insertionConfidence = insertionConfidence ==null?null:Arrays.copyOf(insertionConfidence, insertionConfidence.length);
        return this;
    }

    /**
     * @return the deletionConfidence
     */
    public byte[] deletionConfidence() {
        return deletionConfidence==null?null:Arrays.copyOf(deletionConfidence, deletionConfidence.length);
    }

    /**
     * @param deletionConfidence the deletionConfidence to set
     */
    public SCFChromatogramBuilder deletionConfidence(byte[] deletionConfidence) {
        this.deletionConfidence = deletionConfidence ==null?null:Arrays.copyOf(deletionConfidence, deletionConfidence.length);
        
        return this;
    }

    /**
     * @return the privateData
     */
    public byte[] privateData() {
        return privateData==null? null:Arrays.copyOf(privateData, privateData.length);
    }

    /**
     * @param privateData the privateData to set
     */
    public SCFChromatogramBuilder privateData(byte[] privateData) {
        this.privateData = privateData==null? null:Arrays.copyOf(privateData, privateData.length);
        return this;
    }

    public SCFChromatogram build() {
        Chromatogram basicChromo = super.build();
        return new SCFChromatogramImpl(basicChromo,
                createOptionalConfidence(substitutionConfidence()),
                createOptionalConfidence(insertionConfidence()),
                createOptionalConfidence(deletionConfidence()),
                createPrivateData());
    }
    private Confidence createOptionalConfidence(byte[] confidence){
        if(confidence ==null){
            return null;
        }
        return new DefaultConfidence(confidence);
    }
    private PrivateData createPrivateData() {
        if(privateData() ==null){
            return null;
        }
        return new PrivateData(privateData());
    }


}
