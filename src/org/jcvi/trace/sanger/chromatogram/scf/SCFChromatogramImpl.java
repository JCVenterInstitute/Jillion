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
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof SCFChromatogramImpl)){
            return false;
        }
        final SCFChromatogramImpl other = (SCFChromatogramImpl) obj;
        return CommonUtil.similarTo(getDeletionConfidence(), other.getDeletionConfidence())
            && CommonUtil.similarTo(getInsertionConfidence(), other.getInsertionConfidence())
            && CommonUtil.similarTo(getSubstitutionConfidence(), other.getSubstitutionConfidence())
             && CommonUtil.similarTo(getPrivateData(), other.getPrivateData());


    }


}
