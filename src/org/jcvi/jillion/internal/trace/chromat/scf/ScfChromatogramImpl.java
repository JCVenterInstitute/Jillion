/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.internal.trace.chromat.BasicChromatogram;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.scf.PrivateData;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;

public class ScfChromatogramImpl extends BasicChromatogram implements ScfChromatogram {

    private PrivateData privateData;

    private QualitySequence substitutionConfidence;
    private QualitySequence insertionConfidence;
    private QualitySequence deletionConfidence;

    public ScfChromatogramImpl(Chromatogram c){
        this(c,null,null,null,null);
    }
    public ScfChromatogramImpl(Chromatogram c,
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
