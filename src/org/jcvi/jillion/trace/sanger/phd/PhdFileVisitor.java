/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Nov 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.phd;

import java.util.Properties;

import org.jcvi.jillion.core.io.TextFileVisitor;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code PhdFileVisitor} is a {@link TextFileVisitor}
 * for visiting Phd encoded files.
 * This interface can visit single phd files
 * or multiple phd records either, usually stored
 * in phd.ball files.
 * @author dkatzel
 *
 */
public interface PhdFileVisitor extends TextFileVisitor{
	
    void visitBeginSequence(String id);
    
    void visitEndSequence();
    
    void visitComment(Properties comments);
    
    void visitBeginDna();
    
    void visitEndDna();
    
    void visitBasecall(Nucleotide base, PhredQuality quality, int tracePosition);

    void visitBeginTag(String tagName);
    
    void visitEndTag();
    
    boolean visitBeginPhd(String id);
    /**
     * The current Phd record is done being visited.
     * This method will be called before
     * the call to the next line via {@link #visitLine(String)}
     * or if the file has ended, before {@link #visitEndOfFile()}.
     * @return
     */
    boolean visitEndPhd();
    
}
