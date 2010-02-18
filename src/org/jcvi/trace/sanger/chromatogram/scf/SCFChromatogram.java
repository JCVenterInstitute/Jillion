/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import org.jcvi.sequence.Confidence;
import org.jcvi.trace.sanger.chromatogram.Chromatogram;
/**
 * <code>SCFChromatogram</code> is a SCF specific implementation
 * of {@link Chromatogram}.  SCF Chromatograms have additional
 * data such as Substitution, Insertion, and Deletion confidence at each
 * base call.  There is also the possibility of additional "private data".
 * @author dkatzel
 *
 *
 */
public interface SCFChromatogram extends Chromatogram{


    /**
     * {@link SCFChromatogram}s may have additional PrivateData.
     * @return the privateData; or <code>null</code> if there
     * is no {@link PrivateData}.
     */
    PrivateData getPrivateData();
    Confidence getSubstitutionConfidence();
    Confidence getInsertionConfidence();
    Confidence getDeletionConfidence();

}
