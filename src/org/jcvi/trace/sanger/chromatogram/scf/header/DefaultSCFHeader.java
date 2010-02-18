/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.header;

import org.jcvi.CommonUtil;

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
        if (this == obj)
            return true;
        if (!(obj instanceof DefaultSCFHeader)){
            return false;
        }
        final DefaultSCFHeader other = (DefaultSCFHeader) obj;
         return CommonUtil.similarTo(getBasesOffset(), other.getBasesOffset())
             && CommonUtil.similarTo(getCommentOffset(), other.getCommentOffset())
             && CommonUtil.similarTo(getCommentSize(), other.getCommentSize())
             && CommonUtil.similarTo(getNumberOfBases(), other.getNumberOfBases())
             && CommonUtil.similarTo(getNumberOfSamples(), other.getNumberOfSamples())
             && CommonUtil.similarTo(getPrivateDataOffset(), other.getPrivateDataOffset())
             && CommonUtil.similarTo(getPrivateDataSize(), other.getPrivateDataSize())
             && CommonUtil.similarTo(getSampleOffset(), other.getSampleOffset())
             && CommonUtil.similarTo(getSampleSize(), other.getSampleSize())
             && CommonUtil.similarTo(getVersion(), other.getVersion());

    }


}
