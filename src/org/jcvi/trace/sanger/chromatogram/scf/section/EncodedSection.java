/*
 * Created on Sep 15, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.nio.ByteBuffer;

public class EncodedSection {
    private ByteBuffer data;
    private Section section;
    /**
     * @param data
     * @param section
     */
    public EncodedSection(ByteBuffer data, Section section) {
        this.data = data;
        this.section = section;
    }
    /**
     * @return the data
     */
    public ByteBuffer getData() {
        return data;
    }
    /**
     * @return the section
     */
    public Section getSection() {
        return section;
    }


}
