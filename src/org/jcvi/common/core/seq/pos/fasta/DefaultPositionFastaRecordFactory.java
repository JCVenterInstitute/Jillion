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
package org.jcvi.common.core.seq.pos.fasta;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortGlyph;

public class DefaultPositionFastaRecordFactory implements PositionFastaRecordFactory{
 private static final DefaultPositionFastaRecordFactory INSTANCE = new DefaultPositionFastaRecordFactory();
    
    private DefaultPositionFastaRecordFactory(){}
    
    public static DefaultPositionFastaRecordFactory getInstance(){
        return INSTANCE;
    }
    @Override
    public PositionFastaRecord<Sequence<ShortGlyph>> createFastaRecord(
            String id, String comments, String recordBody) {
        return PositionsFastaRecordUtil.buildFastaRecord(id, comments, recordBody);
        
    }

    @Override
    public PositionFastaRecord<Sequence<ShortGlyph>> createFastaRecord(
            String id, String recordBody) {
        return createFastaRecord(id, null,recordBody);
    }
}
