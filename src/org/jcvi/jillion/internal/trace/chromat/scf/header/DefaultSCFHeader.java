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
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.header;

import org.jcvi.jillion.core.util.ObjectsUtil;

/**
 * <code>DefaultSCFHeader</code> is an object representation of
 * the header in a SCF File.  The SCFHeader specifies the SCF version
 * as well as offset and sizes of chromatogram data contained in the file.
 * @author dkatzel
 *
 *
 */
public class DefaultSCFHeader implements SCFHeader {

    private int numberOfSamples;
    private int sampleOffset;
    private int numberOfBases;
    private int basesOffset;
    private int commentSize;
    private int commentOffset;
    private float version;
    private byte sampleSize;
    private int privateDataSize;
    private int privateDataOffset;

    /**
    * {@inheritDoc}
    */
    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    /**
    * {@inheritDoc}
    */
    public void setNumberOfSamples(int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    /**
    * {@inheritDoc}
    */
    public int getSampleOffset() {
        return sampleOffset;
    }

    /**
    * {@inheritDoc}
    */
    public void setSampleOffset(int sampleOffset) {
        this.sampleOffset = sampleOffset;
    }

    /**
    * {@inheritDoc}
    */
    public int getNumberOfBases() {
        return numberOfBases;
    }

    /**
    * {@inheritDoc}
    */
    public void setNumberOfBases(int numberOfBases) {
        this.numberOfBases = numberOfBases;
    }


    /**
    * {@inheritDoc}
    */
    public int getCommentSize() {
        return commentSize;
    }

    /**
    * {@inheritDoc}
    */
    public void setCommentSize(int commentSize) {
        this.commentSize = commentSize;
    }

    /**
    * {@inheritDoc}
    */
    public int getCommentOffset() {
        return commentOffset;
    }

    /**
    * {@inheritDoc}
    */
    public void setCommentOffset(int commentOffset) {
        this.commentOffset = commentOffset;
    }

    /**
    * {@inheritDoc}
    */
    public float getVersion() {
        return version;
    }

    /**
    * {@inheritDoc}
    */
    public void setVersion(float version) {
        this.version = version;
    }

    /**
    * {@inheritDoc}
    */
    public byte getSampleSize() {
        return sampleSize;
    }

    /**
    * {@inheritDoc}
    */
    public void setSampleSize(byte sampleSize) {
        this.sampleSize = sampleSize;
    }



    /**
    * {@inheritDoc}
    */
    public int getPrivateDataSize() {
        return privateDataSize;
    }

    /**
    * {@inheritDoc}
    */
    public void setPrivateDataSize(int privateDataSize) {
        this.privateDataSize = privateDataSize;
    }

    /**
    * {@inheritDoc}
    */
    public int getPrivateDataOffset() {
        return privateDataOffset;
    }

    /**
    * {@inheritDoc}
    */
    public void setPrivateDataOffset(int privateDataOffset) {
        this.privateDataOffset = privateDataOffset;
    }

    /**
    * {@inheritDoc}
    */
    public int getBasesOffset() {
        return basesOffset;
    }

    /**
    * {@inheritDoc}
    */
    public void setBasesOffset(int basesOffset) {
        this.basesOffset = basesOffset;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getBasesOffset();
        result = prime * result + getCommentOffset();
        result = prime * result + getCommentSize();
        result = prime * result + getNumberOfBases();
        result = prime * result + getNumberOfSamples();
        result = prime * result + getPrivateDataOffset();
        result = prime * result + getPrivateDataSize();
        result = prime * result + getSampleOffset();
        result = prime * result + getSampleSize();
        result = prime * result + Float.floatToIntBits(getVersion());
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
        if (!(obj instanceof DefaultSCFHeader)){
            return false;
        }
        final DefaultSCFHeader other = (DefaultSCFHeader) obj;
         return ObjectsUtil.nullSafeEquals(getBasesOffset(), other.getBasesOffset())
             && ObjectsUtil.nullSafeEquals(getCommentOffset(), other.getCommentOffset())
             && ObjectsUtil.nullSafeEquals(getCommentSize(), other.getCommentSize())
             && ObjectsUtil.nullSafeEquals(getNumberOfBases(), other.getNumberOfBases())
             && ObjectsUtil.nullSafeEquals(getNumberOfSamples(), other.getNumberOfSamples())
             && ObjectsUtil.nullSafeEquals(getPrivateDataOffset(), other.getPrivateDataOffset())
             && ObjectsUtil.nullSafeEquals(getPrivateDataSize(), other.getPrivateDataSize())
             && ObjectsUtil.nullSafeEquals(getSampleOffset(), other.getSampleOffset())
             && ObjectsUtil.nullSafeEquals(getSampleSize(), other.getSampleSize())
             && ObjectsUtil.nullSafeEquals(getVersion(), other.getVersion());

    }


}
