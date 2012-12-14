/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.scf.section.impl;


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
