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

package org.jcvi.common.core.symbol.residue.nuc;


/**
 * @author dkatzel
 *
 *
 */
public final class NucleotideSequenceFactory {

    private NucleotideSequenceFactory(){
        throw new IllegalStateException("not instantiable");
    }

    public static ReferenceEncodedNucleotideSequence createReferenceEncoded(NucleotideSequence reference,
            String toBeEncoded, int startOffset){
        return new DefaultReferenceEncodedNucleotideSequence(reference, toBeEncoded, startOffset);
    }
}
