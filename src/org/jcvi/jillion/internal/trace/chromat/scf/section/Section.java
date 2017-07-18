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
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.section;

/**
 * SCF files are broken down into different Sections.
 * Each Section contains different parts of the
 * {@link ScfChromatogram} data.
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
         * Bases is the section where the basecalls, peaks and quality,
         * data is stored.
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
