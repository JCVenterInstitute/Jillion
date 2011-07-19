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

package org.jcvi.common.core.seq.fastx.fasta.pos;

import java.io.File;

import org.jcvi.common.core.seq.fastx.fasta.AbstractLargeFastaRecordIterator;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortGlyph;

/**
 * @author dkatzel
 *
 *
 */
public class LargePositionFastaRecordIterator extends AbstractLargeFastaRecordIterator
            <Sequence<ShortGlyph>,PositionFastaRecord<Sequence<ShortGlyph>>>{

    public static LargePositionFastaRecordIterator createNewIteratorFor(File fastaFile){
        LargePositionFastaRecordIterator iter = new LargePositionFastaRecordIterator(fastaFile);
               iter.start();           
           
           return iter;
       }
    /**
     * @param fastaFile
     * @param recordFactory
     */
    public LargePositionFastaRecordIterator(
            File fastaFile) {
        super(fastaFile, DefaultPositionFastaRecordFactory.getInstance());
    }

    

}
