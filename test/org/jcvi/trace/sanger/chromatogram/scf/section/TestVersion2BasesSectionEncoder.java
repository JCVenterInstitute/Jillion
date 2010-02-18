/*
 * Created on Sep 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

public class TestVersion2BasesSectionEncoder extends AbstractTestBasesSectionEncoder {

    @Override
    protected AbstractTestBasesSection createAbstractTestBasesSection() {
        return new TestVersion2BasesSection();
    }
}
