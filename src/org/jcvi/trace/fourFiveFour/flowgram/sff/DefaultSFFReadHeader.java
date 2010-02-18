/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.jcvi.CommonUtil;
import org.jcvi.Range;

public class DefaultSFFReadHeader implements SFFReadHeader {
    private final short headerLength;
    private final int numberOfBases;
    private final Range qualityClip;
    private final Range adapterClip;
    private final String name;


    /**
     * @param headerLength
     * @param numberOfBases
     * @param qualityClip
     * @param adapterClip
     * @param name
     */
    public DefaultSFFReadHeader(short headerLength, int numberOfBases,
            Range qualityClip, Range adapterClip, String name) {
        this.headerLength = headerLength;
        this.numberOfBases = numberOfBases;
        this.qualityClip = qualityClip;
        this.adapterClip = adapterClip;
        this.name = name;
        
    }

    @Override
    public Range getAdapterClip() {
        return adapterClip;
    }

    @Override
    public short getHeaderLength() {
        return headerLength;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumberOfBases() {
        return numberOfBases;
    }

    @Override
    public Range getQualityClip() {
        return qualityClip;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + headerLength;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + numberOfBases;
        result = prime * result
                + ((qualityClip == null) ? 0 : qualityClip.hashCode());
        result = prime * result
        + ((adapterClip == null) ? 0 : adapterClip.hashCode());
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DefaultSFFReadHeader)){
            return false;
        }
        final DefaultSFFReadHeader other = (DefaultSFFReadHeader) obj;
        return CommonUtil.similarTo(getHeaderLength(), other.getHeaderLength())
        && CommonUtil.similarTo(getName(), other.getName())
        && CommonUtil.similarTo(getNumberOfBases(), other.getNumberOfBases())
        && CommonUtil.similarTo(getAdapterClip(), other.getAdapterClip())
        && CommonUtil.similarTo(getQualityClip(), other.getQualityClip());

    }


    public static class Builder implements org.jcvi.Builder<DefaultSFFReadHeader>{
        private  short headerLength;
        private  int numberOfBases;
        private  Range qualityClip;
        private  Range adapterClip;
        private  String name;
        
        public Builder(SFFReadHeader header){
            this.headerLength = header.getHeaderLength();
            this.numberOfBases = header.getNumberOfBases();
            this.qualityClip = header.getQualityClip();
            this.adapterClip = header.getAdapterClip();
            this.name = header.getName();
        }
        
        public Builder qualityClip(Range clip){
            this.qualityClip = clip;
            return this;
        }
        @Override
        public DefaultSFFReadHeader build() {
            return new DefaultSFFReadHeader(
                    headerLength, numberOfBases, 
                    qualityClip, adapterClip, name);
        }
        
    }

}
