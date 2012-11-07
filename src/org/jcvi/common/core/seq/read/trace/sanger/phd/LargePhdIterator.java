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

package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;

import org.jcvi.common.core.seq.read.trace.sanger.PositionSequence;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.impl.AbstractBlockingCloseableIterator;

/**
 * {@code LargePhdIterator} is a {@link CloseableIterator}
 * implementation that iterates over a phd ball (potentially large)
 * file while parsing it.  This should be the fastest and most
 * memory efficient way to iterate over a phd ball file.
 * @author dkatzel
 *
 */
public final class LargePhdIterator extends AbstractBlockingCloseableIterator<Phd>{
    private final File phdFile;
        
    
    
    public static LargePhdIterator createNewIterator(File phdFile){
        LargePhdIterator iter= new LargePhdIterator(phdFile);
        iter.start();
        return iter;
    }
    private LargePhdIterator(File phdFile) {
        this.phdFile = phdFile;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {
        PhdFileVisitor visitor = new AbstractPhdFileVisitor() {
            
            @Override
			protected boolean visitPhd(String id, NucleotideSequence bases,
					QualitySequence qualities, PositionSequence positions,
					Properties comments, List<PhdTag> tags) {
            	 Phd phd = new DefaultPhd(id,
                 		bases,
                         qualities,
                         positions,
                         comments,
                         tags);
                 blockingPut(phd);
                 return !LargePhdIterator.this.isClosed();  
			}

       
            
        };
        
        try {
            PhdParser.parsePhd(phdFile, visitor);
        } catch (FileNotFoundException e) {
           throw new RuntimeException(
                   String.format("phd file %s does not exist",phdFile.getAbsolutePath()),
                   e);
        }
    }

}
