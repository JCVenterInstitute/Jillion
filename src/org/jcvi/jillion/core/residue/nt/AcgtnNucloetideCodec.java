/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.nt;

import java.util.Collections;
import java.util.List;

/**
 * {@code AcgtnNucloetideCodec} is a special version
 * of {@link TwoBitEncodedNucleotideCodec} that
 * does not have any gaps.  This makes
 * the computations of gap locations and number of gaps etc
 * trivial to compute (and can be hard coded).
 * @author dkatzel
 *
 *
 */
final class AcgtnNucloetideCodec extends AbstractTwoBitEncodedNucleotideCodec{

    public static final AcgtnNucloetideCodec INSTANCE = new AcgtnNucloetideCodec();
    /**
     * @param sententialBase
     */
    private AcgtnNucloetideCodec() {
        super(Nucleotide.Unknown);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public List<Integer> getGapOffsets(byte[] encodedGlyphs) {
        return Collections.emptyList();
    }


	/**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGaps(byte[] encodedGlyphs) {
        return 0;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isGap(byte[] encodedGlyphs, int gappedOffset) {
        return false;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getUngappedLength(byte[] encodedGlyphs) {
        return decodedLengthOf(encodedGlyphs);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGapsUntil(byte[] encodedGlyphs, int gappedOffset) {
        return 0;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getUngappedOffsetFor(byte[] encodedGlyphs, int gappedOffset) {
        return gappedOffset;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getGappedOffsetFor(byte[] encodedGlyphs, int ungappedOffset) {
        return ungappedOffset;
    }

}
