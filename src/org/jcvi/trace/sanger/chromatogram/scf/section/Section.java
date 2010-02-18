/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import org.jcvi.sequence.Peaks;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramImpl;

/**
 * SCF files are broken down into different Sections.
 * Each Section contains different parts of the
 * {@link SCFChromatogramImpl} data.
 * @author dkatzel
 *
 *
 */
public enum Section {
        /**
         * Samples is the Section where the Chromatogram
         * Position data is stored for each channel.
         */
        SAMPLES,
        /**
         * Bases is the section where the basecalls, {@link Peaks},
         * and {@link Confidence} data is stored.
         */
        BASES,
        /**
         * Comments is the section where meta data about the
         * sequencing run is stored.
         */
        COMMENTS,
        /**
         * SCF allows for additional "private data" to be
         * stored, the format for private data is unspecified
         * and optional.
         */
        PRIVATE_DATA

}
