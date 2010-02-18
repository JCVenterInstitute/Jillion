/*
 * Created on Sep 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;


public class TestVersion3BasesSectionDecoder extends AbstractTestBasesSectionDecoder {

    @Override
    protected AbstractTestBasesSection createAbstractTestBasesSection() {
        return new TestVersion3BasesSection();
    }



}
