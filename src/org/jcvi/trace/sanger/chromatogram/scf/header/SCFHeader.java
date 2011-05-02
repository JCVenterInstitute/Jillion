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
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.header;

import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramImpl;
import org.jcvi.trace.sanger.chromatogram.scf.section.Section;

/**
 * An SCF Header  describes the location and
 * size of the different {@link Section}s of an {@link SCFChromatogram}.
 * @author dkatzel
 *
 *
 */
public interface SCFHeader {

    /**
     * @return the numberOfSamples
     */
    int getNumberOfSamples();

    /**
     * @param numberOfSamples the numberOfSamples to set
     */
    void setNumberOfSamples(int numberOfSamples);

    /**
     * @return the sampleOffset
     */
    int getSampleOffset();

    /**
     * @param sampleOffset the sampleOffset to set
     */
    void setSampleOffset(int sampleOffset);

    /**
     * @return the numberOfBases
     */
    int getNumberOfBases();

    /**
     * @param numberOfBases the numberOfBases to set
     */
    void setNumberOfBases(int numberOfBases);

    /**
     * @return the commentSize
     */
    int getCommentSize();

    /**
     * @param commentSize the commentSize to set
     */
    void setCommentSize(int commentSize);

    /**
     * @return the commentOffset
     */
    int getCommentOffset();

    /**
     * @param commentOffset the commentOffset to set
     */
    void setCommentOffset(int commentOffset);

    /**
     * @return the version
     */
    float getVersion();

    /**
     * @param version the version to set
     */
    void setVersion(float version);

    /**
     * @return the sampleSize
     */
    byte getSampleSize();

    /**
     * @param sampleSize the sampleSize to set
     */
    void setSampleSize(byte sampleSize);

    /**
     * @return the privateDataSize
     */
    int getPrivateDataSize();

    /**
     * @param privateDataSize the privateDataSize to set
     */
    void setPrivateDataSize(int privateDataSize);

    /**
     * @return the privateDataOffset
     */
    int getPrivateDataOffset();

    /**
     * @param privateDataOffset the privateDataOffset to set
     */
    void setPrivateDataOffset(int privateDataOffset);

    /**
     * @return the basesOffset
     */
    int getBasesOffset();

    /**
     * @param basesOffset the basesOffset to set
     */
    void setBasesOffset(int basesOffset);

}
