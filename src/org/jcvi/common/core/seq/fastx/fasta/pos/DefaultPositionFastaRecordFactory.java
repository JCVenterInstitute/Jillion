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
 * Created on Jan 27, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jcvi.common.core.symbol.DefaultShortGlyphCodec;
import org.jcvi.common.core.symbol.EncodedSequence;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortGlyphFactory;
import org.jcvi.common.core.symbol.ShortSymbol;

final class DefaultPositionFastaRecordFactory implements PositionFastaRecordFactory{
 private static final DefaultPositionFastaRecordFactory INSTANCE = new DefaultPositionFastaRecordFactory();
 private static final ShortGlyphFactory GLYPH_FACTORY  = ShortGlyphFactory.getInstance();
 
    private DefaultPositionFastaRecordFactory(){}
    
    public static DefaultPositionFastaRecordFactory getInstance(){
        return INSTANCE;
    }
    
    private static PositionSequenceFastaRecord<Sequence<ShortSymbol>> buildFastaRecord(
            String identifier, String comment, CharSequence sequence) {
        List<ShortSymbol> positions = parsePositions(sequence);
        return new DefaultPositionSequenceFastaRecord<Sequence<ShortSymbol>>(identifier, comment, 
                new EncodedSequence<ShortSymbol>(DefaultShortGlyphCodec.getInstance(),positions));
    }

    private static List<ShortSymbol> parsePositions(CharSequence sequence) {
        Scanner scanner = new Scanner(sequence.toString());
        List<ShortSymbol> result = new ArrayList<ShortSymbol>();
        while(scanner.hasNextShort()){
            result.add(GLYPH_FACTORY.getGlyphFor(scanner.nextShort()));
        }
        return result;
    }
    
    @Override
    public PositionSequenceFastaRecord<Sequence<ShortSymbol>> createFastaRecord(
            String id, String comments, String recordBody) {
        return buildFastaRecord(id, comments, recordBody);
        
    }

    @Override
    public PositionSequenceFastaRecord<Sequence<ShortSymbol>> createFastaRecord(
            String id, String recordBody) {
        return createFastaRecord(id, null,recordBody);
    }
    
   

   
}
