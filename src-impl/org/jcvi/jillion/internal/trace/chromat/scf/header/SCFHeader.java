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
/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.header;

import org.jcvi.jillion.internal.trace.chromat.scf.section.Section;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;

/**
 * An SCF Header  describes the location and
 * size of the different {@link Section}s of an {@link ScfChromatogram}.
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
