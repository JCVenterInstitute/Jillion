/*
 * Created on Sep 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger;

import java.io.File;
import java.io.IOException;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Peaks;

public class DefaultFileSangerTrace implements FileSangerTrace {

    private final SangerTrace trace;
    private final File file;
    
    /**
     * @param trace
     * @param file
     */
    public DefaultFileSangerTrace(SangerTrace trace, File file) {
        this.trace = trace;
        this.file = file;
    }

    @Override
    public File getFile() throws IOException {
        return file;
    }

    @Override
    public int getNumberOfTracePositions() {
        return trace.getNumberOfTracePositions();
    }

    @Override
    public Peaks getPeaks() {
        return trace.getPeaks();
    }

    @Override
    public NucleotideEncodedGlyphs getBasecalls() {
        return trace.getBasecalls();
    }

    @Override
    public EncodedGlyphs<PhredQuality> getQualities() {
        return trace.getQualities();
    }

}
